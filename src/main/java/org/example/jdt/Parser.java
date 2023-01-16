package org.example.jdt;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.example.error.BindingCannotResolvedException;
import org.example.jdt.formator.GenericFormatter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class Parser {
    public ClassInfo parse(Path filePath) throws IOException, BindingCannotResolvedException {
        return parse(getFileContent(filePath), filePath.getFileName().toString());
    }

    public ClassInfo parse(char[] fileContent, String fileName) throws BindingCannotResolvedException {
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setResolveBindings(true);
        parser.setEnvironment(new String[]{"target/classes"}, new String[]{"src/"}, new String[]{"UTF-8"}, true);
        parser.setUnitName(fileName);
        parser.setBindingsRecovery(true);
        parser.setSource(fileContent);

        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        if (!unit.getAST().hasResolvedBindings()) {
            throw new BindingCannotResolvedException("Class binding cannot resolved");
        }
        ClassInfo classInfo = new ClassInfo();
        ClassInfoGenVisitor visitor = new ClassInfoGenVisitor(classInfo);
        unit.accept(visitor);

        return classInfo;
    }

    /**
     * Get file content as char array
     *
     * @param filePath file path
     * @return char array
     */
    private char[] getFileContent(Path filePath) throws IOException {
        // CRC32: 945ec677
        byte[] bytes;
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(filePath))) {
            bytes = new byte[in.available()];
            if (in.read(bytes) != bytes.length) {
                throw new IOException("File content is not fully read");
            }
        }
        return new String(bytes).toCharArray();
    }

    public static void main(String[] args) throws IOException, BindingCannotResolvedException {
        Parser parser = new Parser();
        ClassInfo classInfo = parser.parse(Path.of("src/test/java/org/example/jdt/mocked/Class4ParserTest.java"));
        new GenericFormatter(System.out).format(Collections.singletonList(classInfo));
    }
}
