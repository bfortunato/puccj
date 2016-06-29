package applica.puccj.transformer;

import applica.puccj.linkage.Linkage;
import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.TypeUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

/**
 * Created by bimbobruno on 15/10/15.
 */
public class OriginalToProxyConstructorVisitor extends MethodVisitor {

    private final String className;
    private final String superName;
    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final String[] exceptions;
    private final List<String> allowedPackages;
    private boolean superCalled = false;
    private int labelIndex = 0;
    private Label lastLabel;

    public OriginalToProxyConstructorVisitor(String className, String superName, int access, String name, String desc, String signature, String[] exceptions, List<String> allowedPackages, MethodVisitor mv) {
        super(Opcodes.ASM5, mv);

        this.className = className;
        this.superName = superName;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
        this.allowedPackages = allowedPackages;

    }

    @Override
    public void visitLabel(Label label) {
        labelIndex++;
        superCalled = labelIndex > 1;

        if (!superCalled) {
            super.visitLabel(label);
            lastLabel = label;
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (superCalled) {
            return;
        }

        if (Opcodes.RETURN == opcode) {
            return;
        }

        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (superCalled) {
            return;
        }

        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (superCalled) {
            return;
        }

        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (superCalled) {
            return;
        }

        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (superCalled) {
            return;
        }

        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (superCalled) {
            return;
        }

        if (owner.equals(superName) && name.equals("<init>")) {
            superCalled = true;
        }

        super.visitMethodInsn(opcode, owner, name, TypeUtils.checkedDesc(allowedPackages, desc), itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        if (superCalled) {
            return;
        }

        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (superCalled) {
            return;
        }

        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (superCalled) {
            return;
        }
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        if (superCalled) {
            return;
        }

        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        if (superCalled) {
            return;
        }

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (superCalled) {
            return;
        }

        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        if (superCalled) {
            return;
        }

        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if (superCalled) {
            return;
        }

        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (superCalled) {
            return;
        }

        super.visitLineNumber(line, start);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        end = lastLabel;
        if (TypeUtils.isPackageAllowed(allowedPackages, className)) {
            if (!name.equals("this")) {
                super.visitLocalVariable(name, TypeUtils.checkedTypeDesc(allowedPackages, desc), TypeUtils.checkedSignature(allowedPackages, signature), start, end, index);
                return;
            }
        }

        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitEnd() {
        InstructionAdapter i = new InstructionAdapter(mv);
        Type checkedClassType = TypeUtils.checkedType(allowedPackages, Type.getObjectType(className));

        //create instance id instructions
        i.invokestatic(
                ClassNameUtils.toInternalName(DynamicRuntime.class.getName()),
                "instance",
                Type.getMethodDescriptor(Type.getObjectType(ClassNameUtils.toInternalName(DynamicRuntime.class.getName()))),
                false
        );
        i.invokevirtual(
                ClassNameUtils.toInternalName(DynamicRuntime.class.getName()),
                "nextInstanceId",
                Type.getMethodDescriptor(Type.LONG_TYPE),
                false
        );

        int numberOfArguments = Type.getMethodType(desc).getArgumentsAndReturnSizes();

        i.store(numberOfArguments, Type.LONG_TYPE);
        i.load(0, checkedClassType);
        i.load(numberOfArguments, Type.LONG_TYPE);
        i.putfield(className, Methods.INSTANCE_ID_FIELD, Type.LONG_TYPE.getDescriptor());



        //create proxy call

        MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
        Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapConstructor", mt.toMethodDescriptorString());
        Type methodType = Type.getMethodType(desc);
        Type[] originalArgumentTypes = methodType.getArgumentTypes();
        int extraArguments = 4;
        int index = 0;
        Type[] argumentTypes = new Type[originalArgumentTypes.length + extraArguments + 1];
        argumentTypes[index++] = Type.getObjectType(className);
        //argumentTypes[index++] = Type.getObjectType(className);
        for (Type oat : originalArgumentTypes) {
            argumentTypes[index++] = oat;
        }
        argumentTypes[index++] = Type.getObjectType("java/lang/String");
        argumentTypes[index++] = Type.getObjectType("java/lang/String");
        argumentTypes[index++] = Type.getObjectType("java/lang/String");
        argumentTypes[index++] = Type.BOOLEAN_TYPE;

        Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(Type.VOID_TYPE, argumentTypes));

        i.load(0, checkedClassType);

        //load local parameters
        int localIndex = 1;
        for (Type argumentType : methodType.getArgumentTypes()) {
            i.load(localIndex, TypeUtils.checkedType(allowedPackages, argumentType));
            localIndex++;
        }

        i.aconst(className);
        i.aconst(Methods.CONSTRUCTOR(className));
        i.aconst(TypeUtils.checkedDesc(allowedPackages, desc));
        i.iconst(0);

        i.invokedynamic(Methods.CONSTRUCTOR(className), dynamicMethodType.getInternalName(), bsm, new Object[0]);

        i.visitInsn(Opcodes.RETURN);
        i.visitMaxs(numberOfArguments + 5, numberOfArguments);


        super.visitEnd();
    }
}
