package com.sandbox.java8;

import org.junit.Test;

import java.util.stream.Stream;

public class Java8FeatureTest {

    @Test
    public void name() {
        Stream.of(1, 2,3)
                .filter(n -> n > 0)
                .count();

        Stream.of(1, 2,3)
                .mapToLong(e -> 1)
                .sum();
    }
}
