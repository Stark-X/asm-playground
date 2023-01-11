package org.example.jdt;


import java.util.ArrayList;
import java.util.List;

public class MethodInfo {
    private String name;
    private String digest;
    private final List<String> parameters = new ArrayList<>();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParameters(List<String> parameters) {
        this.parameters.addAll(parameters);
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getDigest() {
        return digest;
    }

    public static class Builder {
        private final MethodInfo ret = new MethodInfo();

        public Builder setName(String name) {
            ret.setName(name);
            return this;
        }

        public Builder setParameters(List<String> params) {
            ret.setParameters(params);
            return this;
        }

        public Builder setDigest(String hash) {
            ret.setDigest(hash);
            return this;
        }

        public MethodInfo build() {
            return ret;
        }

    }
}
