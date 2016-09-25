package com.sandbox.asm;

import org.junit.Test;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DependenciesAnalyzerTest {

    @Test
    public void dependencies() throws IOException {
        Set<String> dependencies = new DependenciesAnalyzer().findDependencies("com.sandbox.asm.Root", "com.sandbox.asm");
        assertThat(dependencies).containsOnly(
                "com.sandbox.asm.Constructor",
                "com.sandbox.asm.Field",
                "com.sandbox.asm.Method",
                "com.sandbox.asm.Root$1",
                "com.sandbox.asm.Root$2",
                "com.sandbox.asm.Lambda",
                "com.sandbox.asm.TransitiveLevel1",
                "com.sandbox.asm.TransitiveLevel2",
                "com.sandbox.asm.TransitiveLevel3",
                "com.sandbox.asm.Parent",
                "com.sandbox.asm.Parent$1"
        );
    }

    @Test
    public void dependenciesOfArrayList() throws IOException {
        Set<String> dependencies = new DependenciesAnalyzer().findDependencies("java.util.ArrayList", "");
        assertThat(dependencies.size()).isGreaterThan(3000);
    }
}
