package org.example.jdt;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.util.HashSet;
import java.util.Set;

public class ClassInfo {
    private final Set<String> methodHashes = new HashSet<>();

    public void addMethod(String methodContent) {
        addMethod(methodContent.getBytes());
    }

    public void addMethod(byte[] methodContent) {
        HashCode hashCode = Hashing.crc32().hashBytes(methodContent);
        methodHashes.add(hashCode.toString());
    }

    public Set<String> getMethodHashes() {
        return methodHashes;
    }

    public boolean containMethod(String methodContent) {
        return containMethod(methodContent.getBytes());
    }

    public boolean containMethod(byte[] methodContent) {
        HashCode hashCode = Hashing.crc32().hashBytes(methodContent);
        return methodHashes.contains(hashCode.toString());
    }
}
