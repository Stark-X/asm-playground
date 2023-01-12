package org.example.jdt.formator;

import org.example.jdt.ClassInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public abstract class AFormatter {
    private final OutputStream out;

    AFormatter(OutputStream out) {
        this.out = out;
    }

    public void format(List<ClassInfo> classInfos) throws IOException {
        for (ClassInfo classInfo : classInfos) {
            out.write(formatEach(classInfo));
        }
    }

    protected abstract byte[] formatEach(ClassInfo classInfo);
}
