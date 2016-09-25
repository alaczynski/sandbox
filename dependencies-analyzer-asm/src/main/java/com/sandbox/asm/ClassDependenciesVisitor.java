package com.sandbox.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM5;

public class ClassDependenciesVisitor extends ClassVisitor {

    public ClassDependenciesVisitor() {
        super(ASM5);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodVisitor(api) {
        };
    }
}
