package org.example.jdt.formator;

import org.example.jdt.ClassInfo;
import org.example.jdt.MethodInfo;

import javax.annotation.Nonnull;
import java.io.OutputStream;

public class GenericFormatter extends AFormatter {
    public GenericFormatter(OutputStream out) {
        super(out);
    }

    @Override
    protected byte[] formatEach(ClassInfo classInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatClassName(classInfo));
        classInfo.getMethodsInfo().forEach(methodInfo -> sb.append(formatMethodInfo(methodInfo)));

        classInfo.getInnerClasses().forEach(innerClassInfo -> {
            sb.append(formatClassName(innerClassInfo));
            innerClassInfo.getMethodsInfo().forEach(methodInfo -> sb.append(formatMethodInfo(methodInfo)));
        });
        return sb.toString().getBytes();
    }

    private static String formatMethodInfo(@Nonnull MethodInfo methodInfo) {
        return String.format("Method: %s, Parameters: %s, Digest: %s%n", methodInfo.getName(), methodInfo.getParameters(), methodInfo.getDigest());
    }

    private static String formatClassName(@Nonnull ClassInfo classInfo) {
        return String.format("----- Class %s -----%n", classInfo.getBinaryName());
    }
}
