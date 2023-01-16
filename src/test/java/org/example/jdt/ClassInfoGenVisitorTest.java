package org.example.jdt;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.example.error.BindingCannotResolvedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ClassInfoGenVisitorTest {
    private final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
    private final File anonClassFile = new File("src/test/java/org/example/jdt/mocked/AnonClassCase.java");
    private final File interfaceFile = new File("src/test/java/org/example/jdt/mocked/InterfaceCase.java");
    private final File innerClassFile = new File("src/test/java/org/example/jdt/mocked/InnerClassCase.java");

    @BeforeEach
    public void setUp() {
        this.parser.setResolveBindings(true);
        this.parser.setEnvironment(new String[]{"target/test-classes", "target/classes"}, new String[]{"src"}, new String[]{"UTF-8"}, true);
        this.parser.setBindingsRecovery(true);
    }

    private ASTNode buildASTNode(File file) throws IOException {
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        parser.setUnitName(file.getName());
        parser.setSource(content.toCharArray());

        return parser.createAST(null);
    }

    @Test
    void should_not_visit_the_anonymous_class_declaration() throws IOException {
        boolean[] probe = {true};
        ClassInfoGenVisitor visitor = new ClassInfoGenVisitor(new ClassInfo()) {
            @Override
            public boolean visit(AnonymousClassDeclaration node) {
                probe[0] = super.visit(node);
                return probe[0];
            }
        };

        ASTNode root = buildASTNode(anonClassFile);
        root.accept(visitor);

        assertFalse(probe[0]);
    }

    @Test
    void should_not_visit_interface() throws IOException {
        boolean[] probe = {true};
        ClassInfoGenVisitor visitor = new ClassInfoGenVisitor(new ClassInfo()) {
            @Override
            public boolean visit(TypeDeclaration node) {
                probe[0] = super.visit(node);
                return probe[0];
            }
        };

        ASTNode root = buildASTNode(interfaceFile);
        root.accept(visitor);

        assertFalse(probe[0]);
    }

    @Test
    void should_store_InnerClass_class_info() throws IOException {
        ClassInfo classInfo = new ClassInfo();
        ClassInfoGenVisitor visitor = new ClassInfoGenVisitor(classInfo);

        ASTNode root = buildASTNode(innerClassFile);
        root.accept(visitor);

        assertFalse(classInfo.getInnerClasses().isEmpty());
        assertEquals("org.example.jdt.mocked.InnerClassCase$Inner", classInfo.getInnerClasses().get(0).getBinaryName());
    }

    @Test
    void should_raise_exception_given_not_resolve_binding() throws IOException {
        ClassInfo classInfo = new ClassInfo();
        ClassInfoGenVisitor visitor = new ClassInfoGenVisitor(classInfo);

        parser.setResolveBindings(false);
        ASTNode root = buildASTNode(innerClassFile);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            root.accept(visitor);
        });
        assertEquals(BindingCannotResolvedException.class, thrown.getCause().getClass());
        assertEquals("Class binding cannot resolved", thrown.getCause().getMessage());
    }
}