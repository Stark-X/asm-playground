package org.example.error.wrapper;


import java.util.function.Consumer;
import java.util.function.Function;

public class GenericThrowingFunctionWrapper {
    private GenericThrowingFunctionWrapper() {
    }

    public static <T> Consumer<T> wrapConsumer(ThrowingConsumer<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    /**
     * wrap a Function, and throw RuntimeException. A custom checked exception wrapper could be defined by your own
     *
     * @param throwingFunction the Function to be wrapped
     * @param <T>              param
     * @param <R>              return
     * @return wrapped Function
     */
    public static <T, R> Function<T, R> wrapFunc(ThrowingFunction<T, R, Exception> throwingFunction) {
        return param -> {
            try {
                return throwingFunction.apply(param);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
}