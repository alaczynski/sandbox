package com.sandbox.asm;

import java.util.ArrayList;

public class Root extends Parent {

    Root() {
        new Constructor();
    }

    Field field = new Field();

    void method() {
        new Method();
    }

    void anonymous() {
        new Runnable() {
            @Override
            public void run() {
            }
        };
        new Runnable() {
            @Override
            public void run() {
            }
        };
    }

    void lambda() {
        new ArrayList<>().forEach((string) -> {
            new Lambda();
        });
    }

    void transitive() {
        new TransitiveLevel1();
    }
}
