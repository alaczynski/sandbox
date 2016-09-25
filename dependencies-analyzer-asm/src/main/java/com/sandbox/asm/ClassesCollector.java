package com.sandbox.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Remapper;

import java.util.Set;
import java.util.TreeSet;

public class ClassesCollector extends Remapper {

    private final Set<String> classes = new TreeSet<>();

    @Override
    public String mapDesc(final String desc) {
        if (desc.startsWith("L")) {
            this.addType(desc.substring(1, desc.length() - 1));
        }
        return super.mapDesc(desc);
    }

    @Override
    public String[] mapTypes(final String[] types) {
        for (final String type : types) {
            this.addType(type);
        }
        return super.mapTypes(types);
    }

    private void addType(final String type) {
        Type objectType = Type.getObjectType(type);
        if (objectType.getSort() == Type.OBJECT) {
            this.classes.add(objectType.getClassName());
        }
    }

    @Override
    public String mapType(final String type) {
        if (type == null) {
            return null;
        }
        this.addType(type);
        return super.mapType(type);
    }

    public Set<String> classes() {
        return classes;
    }
}
