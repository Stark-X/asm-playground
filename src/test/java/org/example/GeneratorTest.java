package org.example;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class GeneratorTest {

    static class MyClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if ("com.example.HelloWorld".equals(name)) {
                byte[] bytes = Generator.dump();
                return defineClass(name, bytes, 0, bytes.length);
            }
            throw new ClassNotFoundException("Class not found: " + name);
        }
    }

    @Test
    void should_return_content_when_dump_and_exec_print() throws Exception{
        ClassLoader myClassLoader = new MyClassLoader();
        Class<?> clazz = myClassLoader.loadClass("com.example.HelloWorld");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        assertEquals("toString content", instance.toString());
    }
}