package org.example.jdt;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ClassInfo {
    private final List<MethodInfo> methodsInfo = new ArrayList<>();
    private String binaryName;

    private final List<ClassInfo> innerClasses = new ArrayList<>();

    public void addMethod(MethodInfo methodInfo) {
        this.methodsInfo.add(methodInfo);
    }

    public boolean containMethod(String digest) {
        return methodsInfo.parallelStream().anyMatch(method -> method.getDigest().equals(digest));
    }

    public boolean containMethod(@Nonnull MethodInfo methodInfo) {
        return containMethod(methodInfo.getDigest());
    }

    public boolean containMethod(String name, List<String> parameters) {
        return methodsInfo.parallelStream().anyMatch(method -> method.equals(name, parameters));
    }

    public boolean containMethod(String name, String... parameters) {
        return containMethod(name, Arrays.stream(parameters).collect(Collectors.toUnmodifiableList()));
    }

    public void dropMethodByDigest(String digest) {
        // digest should only exist once, therefore, it's not necessary to iter all items
        Iterator<MethodInfo> it = methodsInfo.iterator();
        while (it.hasNext()) {
            if (it.next().getDigest().equals(digest)) {
                it.remove();
                break;
            }
        }
    }

    public void setBinaryName(String binaryName) {
        this.binaryName = binaryName;
    }

    public String getBinaryName() {
        return binaryName;
    }

    public List<MethodInfo> getMethodsInfo() {
        return methodsInfo;
    }

    public List<ClassInfo> getInnerClasses() {
        return innerClasses;
    }

    public void addInnerClass(ClassInfo innerClass) {
        this.innerClasses.add(innerClass);
    }
}
