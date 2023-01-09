package org.example.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Generator implements Opcodes {
    public static byte[] dumpHelloWorld() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/example/HelloWorld", null, "java/lang/Object", null);

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "toString", "()Ljava/lang/String;", null, null);
            mv.visitCode();
            mv.visitLdcInsn("toString content");
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();
        return cw.toByteArray();
    }

    public static byte[] dumpInterfaceWithFields() {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cw.visit(V1_8, ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE, "com/example/InterfaceWithFields", null, "java/lang/Object", new String[]{"java/lang/Cloneable"});
        {
            FieldVisitor fv = cw.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC, "LESS", "I", null, -1);
            fv.visitEnd();
        }
        {
            FieldVisitor fv = cw.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC, "EQUAL", "I", null, 0);
            fv.visitEnd();
        }
        {
            FieldVisitor fv = cw.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC, "GREATER", "I", null, 1);
            fv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I", null, null);
            mv.visitEnd();
        }

        cw.visitEnd();
        return cw.toByteArray();
    }
}
