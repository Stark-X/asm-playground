package org.example.jdt;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    private final List<MethodInfo> methodsInfo = new ArrayList<>();
    private String binaryName;

    public void addMethod(MethodInfo methodInfo) {
        this.methodsInfo.add(methodInfo);
    }

    public boolean containMethod(MethodInfo methodInfo) {
        return methodsInfo.parallelStream().anyMatch(method -> method.getDigest().equals(methodInfo.getDigest()));
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
}
