package org.example.jdt.mocked;

public class Class4ParserTest {
    private String name;
    private int age;

    public class FirstInner {
        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }
    }

    public static class Builde$r {
        private String name;
        private int age;

        public int getAge() {
            return age;
        }

        public String getName() {
            return this.name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setName(String name) {
            this.name = name;

            Thread fakeThread = new Thread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
