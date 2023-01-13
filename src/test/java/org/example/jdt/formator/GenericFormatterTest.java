package org.example.jdt.formator;

import org.example.jdt.ClassInfo;
import org.example.jdt.MethodInfo;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericFormatterTest {

    @Test
    void format() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        AFormatter formatter = new GenericFormatter(stream);
        ClassInfo classInfo = new ClassInfo();
        classInfo.setBinaryName("Foo");
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setParameters(Stream.of("java.lang.String", "Z").collect(Collectors.toList()));
        methodInfo.setName("bar");
        methodInfo.setDigest("digest123");
        classInfo.addMethod(methodInfo);
        formatter.format(Collections.singletonList(classInfo));

        assertTrue(stream.toString().contains("Class Foo"));
        assertTrue(stream.toString().contains("Method: bar"));
        assertTrue(stream.toString().contains("Parameters: [java.lang.String, Z]"));
        assertTrue(stream.toString().contains("Digest: digest123"));
    }
}