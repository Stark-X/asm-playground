package org.example.jdt.mocked;

public class AnonClassCase {
    public Thread anonymousStuff() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }
}
