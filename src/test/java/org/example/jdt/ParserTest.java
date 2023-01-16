package org.example.jdt;

import org.example.error.BindingCannotResolvedException;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    private final String packageLine = "package hello;\n";
    private final String fileName = "HelloWorld.java";

    @Test
    void should_parse_simple_class() throws BindingCannotResolvedException {
        String classContent = packageLine +
                "class HelloWorld {\n" +
                "    public void sayHello() {\n" +
                "        System.out.println(\"Hello World!\");\n" +
                "    }\n" +
                "}";
        Parser parser = new Parser();

        ClassInfo classInfo = parser.parse(classContent.toCharArray(), fileName);
        assertEquals("hello.HelloWorld", classInfo.getBinaryName());
        assertEquals(1, classInfo.getMethodsInfo().size());

        MethodInfo methodInfo = classInfo.getMethodsInfo().get(0);
        assertEquals("sayHello", methodInfo.getName());
        assertArrayEquals(new String[]{}, methodInfo.getParameters().toArray());
        assertTrue(StringUtils.isNotBlank(methodInfo.getDigest()));
    }

    @Test
    void should_parse_class_with_parameterized_method() throws BindingCannotResolvedException {
        String classContent = packageLine +
                "class HelloWorld {\n" +
                "    public void sayHello(String words, boolean foo) {\n" +
                "        System.out.println(words);\n" +
                "    }\n" +
                "}";
        Parser parser = new Parser();

        ClassInfo classInfo = parser.parse(classContent.toCharArray(), fileName);
        MethodInfo methodInfo = classInfo.getMethodsInfo().get(0);

        List<String> parameters = methodInfo.getParameters();
        assertEquals("java.lang.String", parameters.get(0));
        assertEquals("boolean", parameters.get(1));
    }
}