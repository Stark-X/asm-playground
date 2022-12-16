package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GeneratorTest {

    private static ClassLoader classLoader;

    static class MyClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if ("com.example.HelloWorld".equals(name)) {
                byte[] bytes = Generator.dumpHelloWorld();
                return defineClass(name, bytes, 0, bytes.length);
            }
            if ("com.example.InterfaceWithFields".equals(name)) {
                byte[] bytes = Generator.dumpInterfaceWithFields();
                return defineClass(name, bytes, 0, bytes.length);
            }
            throw new ClassNotFoundException("Class not found: " + name);
        }
    }

    @BeforeAll
    static void setUp() {
        classLoader = new MyClassLoader();
    }

    @Test
    void should_return_content_when_dump_and_exec_print() throws Exception{
        Class<?> clazz = classLoader.loadClass("com.example.HelloWorld");
        Object instance = clazz.getDeclaredConstructor().newInstance();
        assertEquals("toString content", instance.toString());
    }

    @Test
    void should_return_interface_with_three_fields_and_one_method_when_dump() throws Exception {
        Class<?> IClazz = classLoader.loadClass("com.example.InterfaceWithFields");
        assertEquals(IClazz.getDeclaredField("LESS").get(null), -1);
        assertEquals(IClazz.getDeclaredField("EQUAL").get(null), 0);
        assertEquals(IClazz.getDeclaredField("GREATER").get(null), 1);
        assertEquals(IClazz.getDeclaredMethod("compareTo", Object.class).getReturnType(), int.class);
    }
}