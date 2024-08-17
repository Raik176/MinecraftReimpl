package org.rhm;

public class Identifier {

    private final String namespace;
    private final String key;
    private final boolean wasMissingNamespace;

    public Identifier(String key) {
        this.namespace = "minecraft";
        this.key = key;
        this.wasMissingNamespace = true;
    }

    public Identifier(String namespace, String key) {
        this.namespace = namespace;
        this.key = key;
        this.wasMissingNamespace = false;
    }

    public boolean isWasMissingNamespace() {
        return wasMissingNamespace;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return namespace + ":" + key;
    }
}
