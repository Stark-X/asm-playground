package org.example.jdt;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {
    private ClassInfo parseMethodsFromClass(Path filePath) {
        ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
        parser.setSource(getFileContent(filePath));
        CompilationUnit unit = (CompilationUnit) parser.createAST(null);
        ClassInfo classInfo = new ClassInfo();
        DemoVisitor visitor = new DemoVisitor(classInfo);
        unit.accept(visitor);

        return classInfo;
    }

    /** Get file content as char array
     * @param filePath file path
     * @return char array
     */
    private char[] getFileContent(Path filePath) {
        byte[] bytes;
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(filePath))){
            bytes = new byte[in.available()];
            in.read(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(bytes).toCharArray();
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        ClassInfo classInfo = parser.parseMethodsFromClass(Path.of("src/main/java/org/example/jdt/Parser.java"));
        System.out.println(classInfo.containMethod("incorrect method body"));
    }
}
