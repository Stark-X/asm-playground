package org.example.jdt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassInfoTest {

    @Test
    void containMethod() {
        ClassInfo classInfo = new ClassInfo();
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDigest("foobar123");
        classInfo.addMethod(methodInfo);

        assertTrue(classInfo.containMethod(new MethodInfo.Builder().setDigest("foobar123").build()));
    }

    @Test
    void dropMethodByDigest() {
        ClassInfo classInfo = new ClassInfo();

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDigest("foobar123");

        classInfo.addMethod(methodInfo);
        classInfo.addMethod(methodInfo);

        classInfo.dropMethodByDigest("foobar123");
        assertEquals(1, classInfo.getMethodsInfo().size());
        classInfo.dropMethodByDigest("foobar123");
        assertEquals(0, classInfo.getMethodsInfo().size());
    }
}