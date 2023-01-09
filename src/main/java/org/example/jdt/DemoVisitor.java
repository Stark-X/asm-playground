package org.example.jdt;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class DemoVisitor extends ASTVisitor {
    private final ClassInfo classInfo;

    public DemoVisitor(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        classInfo.addMethod(node.getBody().toString());
        return super.visit(node);
    }
}
