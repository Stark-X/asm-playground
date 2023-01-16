package org.example.jdt;

import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.eclipse.jdt.core.dom.*;
import org.example.error.BindingCannotResolvedException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInfoGenVisitor extends ASTVisitor {
    private final ClassInfo classInfo;

    public ClassInfoGenVisitor(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    @Override
    public void preVisit(ASTNode node) {
        if (!node.getAST().hasResolvedBindings()) {
            try {
                throw new BindingCannotResolvedException("Class binding cannot resolved");
            } catch (BindingCannotResolvedException e) {
                throw new RuntimeException(e);
            }
        }
        super.preVisit(node);
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node) {
        // Class is anonymous, and it's used in a method, could be skipped.
        return false;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        // if interface modified, all implementation should be modified together, so skipped
        if (node.isInterface()) {
            return false;
        }

        // first time visit for primary class
        if (Strings.isNullOrEmpty(classInfo.getBinaryName())) {
            classInfo.setBinaryName(node.resolveBinding().getBinaryName());
            visitMethods(node.getMethods());
            return super.visit(node);
        }


        // InnerClass
        ClassInfo innerClassInfo = new ClassInfo();
        ClassInfoGenVisitor visitor = new ClassInfoGenVisitor(innerClassInfo);
        node.accept(visitor);
        classInfo.addInnerClass(innerClassInfo);

        return super.visit(node);
    }

    public void visitMethods(MethodDeclaration[] nodes) {
        Arrays.stream(nodes).forEach(this::visitMethod);
    }

    public void visitMethod(MethodDeclaration node) {
        List<String> params = Stream.of(node.resolveBinding().getParameterTypes())
                .map(ITypeBinding::getQualifiedName)
                .collect(Collectors.toList());

        HashCode digest = Hashing.crc32().hashBytes(node.getBody().toString().getBytes());

        MethodInfo methodInfo = new MethodInfo.Builder()
                .setName(node.getName().toString())
                .setParameters(params)
                .setDigest(digest.toString())
                .build();
        classInfo.addMethod(methodInfo);
    }
}
