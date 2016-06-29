package applica.puccj.transformer;

import applica.puccj.linkage.Linkage;
import applica.puccj.utils.TypeUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

/**
 * Created by bimbobruno on 18/02/16.
 */
public class ProxyCallerMethodVisitor extends InstructionAdapter {

    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final String[] exceptions;
    private final String className;
    private final List<String> allowedPackages;
    private final DynamicClassVisitor cv;
    private final String proxiedMethodName;

    private boolean enabled = false;

    public ProxyCallerMethodVisitor(String className, List<String> allowedPackages, int access, String name, String proxiedMethodName, String desc, String signature, String[] exceptions, MethodVisitor mv, DynamicClassVisitor cv) {
        super(Opcodes.ASM5, mv);

        this.className = className;
        this.access = access;
        this.name = name;
        this.proxiedMethodName = proxiedMethodName;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
        this.cv = cv;
        this.allowedPackages = allowedPackages;
    }

    @Override
    public void visitParameter(String name, int access) {
        if (!enabled) { return; }
        super.visitParameter(name, access);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        if (!enabled) { return; }
        super.visitAttribute(attr);
    }

    @Override
    public void visitCode() {
        if (!enabled) { return; }
        super.visitCode();
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        if (!enabled) { return; }
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        if (!enabled) { return; }
        mv.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (!enabled) { return; }
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (!enabled) { return; }
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (!enabled) { return; }
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (!enabled) { return; }
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (!enabled) { return; }
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (!enabled) { return; }
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        if (!enabled) { return; }
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (!enabled) { return; }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        if (!enabled) { return; }
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (!enabled) { return; }
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        if (!enabled) { return; }
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        if (!enabled) { return; }
        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (!enabled) { return; }
        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        if (!enabled) { return; }
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (!enabled) { return; }
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (!enabled) { return; }
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return super.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return super.visitParameterAnnotation(parameter, desc, visible);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        if (!enabled) { return new NoOpAnnotationVisitor(); }
        return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if (!enabled) { return; }
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        if (!enabled) { return new NoOpAnnotationVisitor(); }
        return super.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (!enabled) { return; }
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        if (!enabled) { return new NoOpAnnotationVisitor(); }
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible);
    }

    @Override
    public void visitEnd() {
        enabled = true;
        boolean isStatic = (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
        boolean isPublic = (access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;
        boolean isInterface = (access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;

        String bootstrapper = "bootstrapVirtual";
        if (isStatic) {
            bootstrapper = "bootstrapStatic";
        } else if (!isPublic) {
            bootstrapper = "bootstrapSpecial";
        } else if (isInterface) {
            bootstrapper = "bootstrapInterface";
        }

        //DynamicInstructionAdapter transform method into a dynamicone
        Type methodType = Type.getMethodType(desc);

        Type[] arguments = methodType.getArgumentTypes();
        int maxStack = 4 + arguments.length;
        int maxLocals = arguments.length;

        MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
        Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), bootstrapper, mt.toMethodDescriptorString());

        Type[] originalArgumentTypes = methodType.getArgumentTypes();
        int extraArguments = 4;
        int index = 0;
        Type[] argumentTypes = new Type[originalArgumentTypes.length + extraArguments + (isStatic ? 0 : 1)];

        if (!isStatic) {
            argumentTypes[index++] = Type.getObjectType(className);
        }

        for (Type oat : originalArgumentTypes) {
            argumentTypes[index++] = oat;
        }
        argumentTypes[index++] = Type.getObjectType("java/lang/String");
        argumentTypes[index++] = Type.getObjectType("java/lang/String");
        argumentTypes[index++] = Type.getObjectType("java/lang/String");
        argumentTypes[index++] = Type.BOOLEAN_TYPE;

        Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

        if (isStatic) {
            index = 0;
        } else {
            index = 1;
        }

        for (Type a : arguments) {
            super.load(index, a);
            index++;
        }

        super.aconst(className);
        super.aconst(proxiedMethodName);
        super.aconst(TypeUtils.checkedDesc(allowedPackages, desc));
        super.iconst(isInterface ? 1 : 0);

        super.invokedynamic(proxiedMethodName, dynamicMethodType.getInternalName(), bsm, new Object[0]);

        super.areturn(methodType.getReturnType());
        super.visitMaxs(maxStack, maxLocals);
        enabled = false;

        super.visitEnd();
    }



}
