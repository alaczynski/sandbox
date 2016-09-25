package com.sandbox.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.ClassRemapper;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class DependenciesAnalyzer {

    public Set<String> findDependencies(String className, String include) throws IOException {
        Set<String> dependencies = new TreeSet<>();
        findDependencies(className, dependencies, include);
        dependencies.remove(className);
        return dependencies;
    }

    private void findDependencies(String className, Set<String> dependencies, String classNamePrefix) throws IOException {
        ClassReader reader = new ClassReader(className);
        ClassesCollector collector = new ClassesCollector();
        ClassRemapper visitor = new ClassRemapper(new ClassDependenciesVisitor(), collector);
        reader.accept(visitor, 0);
        for (String dependentClass : collector.classes()) {
            if (dependentClass.startsWith(classNamePrefix) && !dependencies.contains(dependentClass)) {
                dependencies.add(dependentClass);
                findDependencies(dependentClass, dependencies, classNamePrefix);
            }
        }
    }
}
