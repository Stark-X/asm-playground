package org.example.jdt;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassInfoGenVisitor extends ASTVisitor {
    private final ClassInfo classInfo;

    public ClassInfoGenVisitor(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }


    @Override
    public boolean visit(TypeDeclaration node) {
        classInfo.setBinaryName(node.resolveBinding().getBinaryName());
        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        List<String> params = Stream.of(node.resolveBinding().getParameterTypes())
                .map(ITypeBinding::getBinaryName)
                .collect(Collectors.toList());

        HashCode digest = Hashing.crc32().hashBytes(node.getBody().toString().getBytes());

        MethodInfo methodInfo = new MethodInfo.Builder()
                .setName(node.getName().toString())
                .setParameters(params)
                .setDigest(digest.toString())
                .build();
        classInfo.addMethod(methodInfo);
        return super.visit(node);
    }
}
