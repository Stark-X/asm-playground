package org.example.jdt;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassInfoTest {
    @Test
    void containMethod_name_and_parameter() {
        ClassInfo classInfo = new ClassInfo();
        classInfo.addMethod(
                new MethodInfo.Builder()
                        .setName("foo")
                        .setParameters(Stream.of("int", "int").collect(Collectors.toUnmodifiableList()))
                        .build());

        assertTrue(classInfo.containMethod("foo", "int", "int"));
        assertTrue(classInfo.containMethod("foo", Stream.of("int", "int").collect(Collectors.toUnmodifiableList())));
    }

    @Test
    void containMethod_String() {
        ClassInfo classInfo = new ClassInfo();
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setDigest("foobar123");
        classInfo.addMethod(methodInfo);

        assertTrue(classInfo.containMethod("foobar123"));
    }

    @Test
    void containMethod_MethodInfo() {
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