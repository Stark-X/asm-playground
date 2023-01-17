package org.example.jdt;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodInfoTest {

    @Test
    void should_equals_given_name_and_parameters_equals() {
        MethodInfo foo = new MethodInfo.Builder()
                .setName("foo")
                .setParameters(
                        Stream.of("java.lang.String", "boolean[]").collect(Collectors.toUnmodifiableList())
                )
                .build();
        assertTrue(foo.equals("foo", Stream.of("java.lang.String", "boolean[]").collect(Collectors.toUnmodifiableList())));
    }

    @Test
    void should_not_equals_given_name_equals_and_parameters_order_not_equals() {
        MethodInfo foo = new MethodInfo.Builder()
                .setName("foo")
                .setParameters(
                        Stream.of("java.lang.String", "boolean[]").collect(Collectors.toUnmodifiableList())
                )
                .build();
        assertFalse(foo.equals("foo", Stream.of("boolean[]", "java.lang.String").collect(Collectors.toUnmodifiableList())));
    }
}