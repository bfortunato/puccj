package applica.puccj.transformer;

import applica.puccj.linkage.Linkage;
import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.TypeUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;

/**
 * Created by bimbobruno on 07/10/15.
 */
class DynamicInstructionAdapter extends InstructionAdapter {

    private final String className;
    private final List<String> allowedPackages;
    int dynamics = 0;

    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final String[] exceptions;
    private final DynamicClassVisitor cv;



    public DynamicInstructionAdapter(String className, List<String> allowedPackages, int access, String name, String desc, String signature, String[] exceptions, MethodVisitor mv, DynamicClassVisitor cv) {
        super(Opcodes.ASM5, mv);

        this.className = className;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
        this.cv = cv;
        this.allowedPackages = allowedPackages;
    }

    @Override
    public void invokevirtual(String owner, String name, String desc, boolean itf) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapVirtual", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] originalArgumentTypes = methodType.getArgumentTypes();
            int extraArguments = 4;
            int index = 0;
            Type[] argumentTypes = new Type[originalArgumentTypes.length + extraArguments + 1];
            argumentTypes[index++] = Type.getObjectType(owner);
            for (Type oat : originalArgumentTypes) {
                argumentTypes[index++] = oat;
            }
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.BOOLEAN_TYPE;

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedDesc(allowedPackages, desc));
            super.iconst(itf ? 1 : 0);

            super.invokedynamic(name, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.invokevirtual(owner, name, desc, itf);
        }
    }

    @Override
    public void invokespecial(String owner, String name, String desc, boolean itf) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            //init are ignored. Take a look in DynamicClassVisitor.
            //all init method are empty and inside they calls dynamically a custom constructor that contains the real code
            if ("<init>".equals(name)) {
                super.invokespecial(owner, name, TypeUtils.checkedDesc(allowedPackages, desc), itf);
                return;
            }

            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapSpecial", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] originalArgumentTypes = methodType.getArgumentTypes();
            int extraArguments = 4;
            int index = 0;
            Type[] argumentTypes = new Type[originalArgumentTypes.length + extraArguments + 1];
            argumentTypes[index++] = Type.getObjectType(owner);
            for (Type oat : originalArgumentTypes) {
                argumentTypes[index++] = oat;
            }
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.BOOLEAN_TYPE;

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedDesc(allowedPackages, desc));
            super.iconst(itf ? 1 : 0);

            String callSiteName = name;

            super.invokedynamic(callSiteName, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.invokespecial(owner, name, desc, itf);
        }
    }

    @Override
    public void invokedynamic(String name, String desc, Handle bsm, Object[] bsmArgs) {
        super.invokedynamic(name, TypeUtils.checkedDesc(allowedPackages, desc), bsm, bsmArgs);
    }

    @Override
    public void invokeinterface(String owner, String name, String desc) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            super.visitLabel(new Label());

            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapInterface", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] originalArgumentTypes = methodType.getArgumentTypes();
            int extraArguments = 3;
            int index = 0;
            Type[] argumentTypes = new Type[originalArgumentTypes.length + extraArguments + 1];
            argumentTypes[index++] = Type.getObjectType(owner);
            for (Type oat : originalArgumentTypes) {
                argumentTypes[index++] = oat;
            }
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedDesc(allowedPackages, desc));

            String callSiteName = name;

            super.invokedynamic(callSiteName, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.invokeinterface(owner, name, desc);
        }
    }

    @Override
    public void invokestatic(String owner, String name, String desc, boolean itf) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapStatic", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] originalArgumentTypes = methodType.getArgumentTypes();
            int extraArguments = 4;
            int index = 0;
            Type[] argumentTypes = new Type[originalArgumentTypes.length + extraArguments];
            for (Type oat : originalArgumentTypes) {
                argumentTypes[index++] = oat;
            }
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.BOOLEAN_TYPE;

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedDesc(allowedPackages, desc));
            super.iconst(itf ? 1 : 0);

            super.invokedynamic(name, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.invokestatic(owner, name, desc, itf);
        }
    }

    @Override
    public void getfield(String owner, String name, String desc) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapGetField", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] argumentTypes = new Type[4];
            int index = 0;
            argumentTypes[index++] = Type.getObjectType(owner);
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedTypeDesc(allowedPackages, desc));

            super.invokedynamic(name, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.getfield(owner, name, desc);
        }
    }

    @Override
    public void putfield(String owner, String name, String desc) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapPutField", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] argumentTypes = new Type[5];
            int index = 0;
            argumentTypes[index++] = Type.getObjectType(owner);
            argumentTypes[index++] = TypeUtils.checkedType(allowedPackages, methodType.getReturnType());
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");
            argumentTypes[index++] = Type.getObjectType("java/lang/String");

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(Type.VOID_TYPE, argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedTypeDesc(allowedPackages, desc));

            super.invokedynamic(name, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.putfield(owner, name, desc);
        }
    }

    @Override
    public void getstatic(String owner, String name, String desc) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapGetStatic", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] argumentTypes = new Type[3];
            argumentTypes[0] = Type.getObjectType("java/lang/String");
            argumentTypes[1] = Type.getObjectType("java/lang/String");
            argumentTypes[2] = Type.getObjectType("java/lang/String");

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(methodType.getReturnType(), argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedTypeDesc(allowedPackages, desc));

            super.invokedynamic(name, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.getstatic(owner, name, desc);
        }
    }

    @Override
    public void putstatic(String owner, String name, String desc) {
        if (TypeUtils.isPackageAllowed(allowedPackages, owner)) {
            MethodType mt = MethodType.methodType(CallSite.class, MethodHandles.Lookup.class, String.class, MethodType.class);
            Handle bsm = new Handle(Opcodes.H_INVOKESTATIC, Linkage.class.getName().replace(".", "/"), "bootstrapPutStatic", mt.toMethodDescriptorString());
            Type methodType = Type.getMethodType(desc);
            Type[] argumentTypes = new Type[4];
            argumentTypes[0] = methodType.getReturnType();
            argumentTypes[1] = Type.getObjectType("java/lang/String");
            argumentTypes[2] = Type.getObjectType("java/lang/String");
            argumentTypes[3] = Type.getObjectType("java/lang/String");

            Type dynamicMethodType = TypeUtils.checkedMethodType(allowedPackages, Type.getMethodType(Type.VOID_TYPE, argumentTypes));

            super.aconst(owner);
            super.aconst(name);
            super.aconst(TypeUtils.checkedTypeDesc(allowedPackages, desc));

            super.invokedynamic(name, dynamicMethodType.getInternalName(), bsm, new Object[0]);
        } else {
            super.putstatic(owner, name, desc);
        }
    }

    @Override
    public void checkcast(Type type) {
        //in case of class that use not reloaded class (classes that are loaded in other contexts), for
        //example, a factory that build instance of reloaded classes, but the factory is not reloades (so use old version of classes)
        //the cast check will be removed
        if (TypeUtils.isPackageAllowed(allowedPackages, type.getInternalName())) {
            super.checkcast(TypeUtils.checkedType(allowedPackages, type));
        } else {
            super.checkcast(type);
        }
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (TypeUtils.isPackageAllowed(allowedPackages, className)) {
            if (!name.equals("this")) {
                super.visitLocalVariable(name, TypeUtils.checkedTypeDesc(allowedPackages, desc), TypeUtils.checkedSignature(allowedPackages, signature), start, end, index);
                return;
            }
        }

        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void tconst(Type type) {
        if (TypeUtils.isPackageAllowed(allowedPackages, type.getInternalName())) {
            String classesRegistry = ClassNameUtils.toInternalName(ClassesRegistry.class.getName());
            super.invokestatic(classesRegistry, "instance", String.format("()L%s;", classesRegistry), false);
            super.aconst(type.getInternalName());
            super.invokevirtual(classesRegistry, "__loadOriginalClass", "(Ljava/lang/String;)Ljava/lang/Class;", false);
            super.checkcast(Type.getObjectType("java/lang/Class"));
        } else {
            super.tconst(type);
        }
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void newarray(Type type) {
        super.newarray(TypeUtils.checkedType(allowedPackages, type));
    }
}