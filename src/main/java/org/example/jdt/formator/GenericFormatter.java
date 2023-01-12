package org.example.jdt.formator;

import org.example.jdt.ClassInfo;

import java.io.OutputStream;

public class GenericFormatter extends AFormatter {
    public GenericFormatter(OutputStream out) {
        super(out);
    }

    @Override
    protected byte[] formatEach(ClassInfo classInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("----- Class %s -----%n", classInfo.getBinaryName()));
        classInfo.getMethodsInfo().forEach(methodInfo -> {
            sb.append(String.format("Method: %s, Parameters: %s, Digest: %s%n", methodInfo.getName(), methodInfo.getParameters(), methodInfo.getDigest()));
        });
        return sb.toString().getBytes();
    }
}
