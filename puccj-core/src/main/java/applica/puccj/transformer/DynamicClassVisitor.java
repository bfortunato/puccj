package applica.puccj.transformer;

import applica.puccj.runtime.__PuccjObject;
import applica.puccj.runtime.classes.RefreshConstructorParameter;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.MemoryUtils;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by bimbobruno on 07/10/15.
 */
public class DynamicClassVisitor extends ClassVisitor {

    private String className;
    private List<String> allowedPackages;
    private String superName;
    private boolean isInterface;
    private DynamicClassInfo classInfo;

    public DynamicClassVisitor(String className, List<String> allowedPackages, ClassVisitor cv, DynamicClassInfo classInfo) {
        super(Opcodes.ASM5, cv);

        this.allowedPackages = new ArrayList<>();
        for (String ap : allowedPackages) {
            this.allowedPackages.add(ClassNameUtils.toInternalName(ap));
        }
        this.className = className;
        this.classInfo = classInfo;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        if (!TypeUtils.isPackageAllowed(allowedPackages, name)) {
            throw new RuntimeException("Package not allowed: " + name);
        }

        if (version < Opcodes.V1_7) {
            version = Opcodes.V1_7;
        }

        this.superName = superName;

        SuperType superType = SuperTypesRegistry.instance().getSuperType(name);
        Objects.requireNonNull(superType, "SuperType not found for class: " + name);
        String[] interfacesWithSuperTypes = ArrayUtils.addAll(
                interfaces,
                ClassNameUtils.toInternalName(superType.getType().getName()),
                ClassNameUtils.toInternalName(__PuccjObject.class.getName())
        );

        if ((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE) {
            isInterface = true;
            super.visit(version, access, name, TypeUtils.checkedSignature(allowedPackages, signature), superName, interfacesWithSuperTypes);
            return;
        }

        //make classes public
        if ((access & Opcodes.ACC_PUBLIC) != Opcodes.ACC_PUBLIC) {
            super.visit(version, access | Opcodes.ACC_PUBLIC, name, signature, superName, interfacesWithSuperTypes);
        } else {
            super.visit(version, access, name, TypeUtils.checkedSignature(allowedPackages, signature), superName, interfacesWithSuperTypes);
        }

    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (TypeUtils.isPackageAllowed(allowedPackages, name)) {
            super.visitInnerClass(name, outerName, innerName, access);
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!TypeUtils.isPackageAllowed(allowedPackages, name) && !isInterface) {
            if (name.equals(Methods.INSTANCE_ID_GETTER)) { return null; }
            if (name.equals(Methods.INSTANCE_ID_SETTER)) { return null; }
            if (name.contains(Methods.LOOKUP)) { return null; }
            if (name.contains(Methods.CLASSNAME)) { return null; }
            if (name.equals("finalize") && className.contains("EnhancerByCGLIB")) { return null; }

            String checkedDesc = TypeUtils.checkedDesc(allowedPackages, desc);
            String checkedSignature = TypeUtils.checkedSignature(allowedPackages, signature);

            //make private methods accessible from inherited classes
            if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
                access ^= Opcodes.ACC_PRIVATE;
                access |= Opcodes.ACC_PROTECTED;
            }

            if ("<init>".equals(name)) {
                //if is refresh constructor something is wrong
                if (isRefreshConstructor(desc)) {
                    return null;
                }

                //create an empty <init> method that call the dynamic one
                OriginalToProxyConstructorVisitor otp =
                        new OriginalToProxyConstructorVisitor(
                                className,
                                superName,
                                access,
                                name,
                                checkedDesc,
                                checkedSignature,
                                exceptions,
                                allowedPackages,
                                super.visitMethod(access, name, checkedDesc, checkedSignature, exceptions)
                        );

                String dynamicMethodName = dynamicMethodName = String.format("%s%s%s", TypeUtils.generateDynamicMethodPrefix(className), Methods.CONSTRUCTOR(className), Methods.DYNAMIC_METHOD_SUFFIX);

                //this creates the dynamic constructor, called from the proxy
                DynamicConstructorAdapter dca =
                        new DynamicConstructorAdapter(
                                className,
                                superName,
                                access,
                                dynamicMethodName,
                                checkedDesc,
                                checkedSignature,
                                exceptions,
                                allowedPackages,
                                super.visitMethod(access, dynamicMethodName, checkedDesc, checkedSignature, exceptions)
                        );

                DynamicInstructionAdapter dia =
                        new DynamicInstructionAdapter(
                                className,
                                allowedPackages,
                                access,
                                dynamicMethodName,
                                checkedDesc,
                                checkedSignature,
                                exceptions,
                                dca,
                                this
                        );

                ProxyMethodVisitor pmv = new ProxyMethodVisitor(
                        className,
                        allowedPackages,
                        access,
                        Methods.CONSTRUCTOR(className),
                        dynamicMethodName,
                        checkedDesc,
                        checkedSignature,
                        exceptions,
                        super.visitMethod(access, Methods.CONSTRUCTOR(className), checkedDesc, checkedSignature, exceptions),
                        this
                );

                return new MultiMethodVisitor(otp, dia, pmv);
            } else {
                String dynamicMethodName;
                String originalMethodName;
                if ("<clinit>".equals(name)) {
                    dynamicMethodName = String.format("%s%s%s", TypeUtils.generateDynamicMethodPrefix(className), "clinit", Methods.DYNAMIC_METHOD_SUFFIX);
                    //originalMethodName = String.format("%s%s%s", TypeUtils.generateDynamicMethodPrefix(className), "clinit", Methods.ORIGINAL_METHOD_SUFFIX);
                } else {
                    dynamicMethodName = String.format("%s%s%s", TypeUtils.generateDynamicMethodPrefix(className), name, Methods.DYNAMIC_METHOD_SUFFIX);
                    //originalMethodName = String.format("%s%s%s", TypeUtils.generateDynamicMethodPrefix(className), name, Methods.ORIGINAL_METHOD_SUFFIX);
                }

                DynamicInstructionAdapter dia =
                        new DynamicInstructionAdapter(
                                className,
                                allowedPackages,
                                access,
                                dynamicMethodName,
                                checkedDesc,
                                checkedSignature,
                                exceptions,
                                super.visitMethod(access, dynamicMethodName, checkedDesc, checkedSignature, exceptions),
                                this
                        );

                ProxyMethodVisitor pmv =
                        new ProxyMethodVisitor(
                                className,
                                allowedPackages,
                                access,
                                name,
                                dynamicMethodName,
                                checkedDesc,
                                checkedSignature,
                                exceptions,
                                super.visitMethod(access, name, checkedDesc, checkedSignature, exceptions),
                                this
                        );


                //MethodVisitor omv = super.visitMethod(access, originalMethodName, desc, signature, exceptions);

                return new MultiMethodVisitor(pmv, new RemoveAnnotationsMethodVisitor(dia)/*, omv*/);
            }
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    private boolean isRefreshConstructor(String desc) {
        MethodType mt = MethodType.methodType(void.class, RefreshConstructorParameter.class);
        return mt.toMethodDescriptorString().equals(desc);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (name.equals(Methods.INSTANCE_ID_FIELD)) { return null; }

        return super.visitField(access, name, TypeUtils.checkedTypeDesc(allowedPackages, desc), TypeUtils.checkedSignature(allowedPackages, signature), value);
    }

    @Override
    public void visitEnd() {
        if (TypeUtils.isPackageAllowed(allowedPackages, className) && !isInterface) {
            createInstanceIdField();
            createGetInstanceIdMethod();
            createSetInstanceIdMethod();
            if (!classInfo.isMethodDefined("finalize", "()V")) {
                createFinalizeMethod();
            }
            //createPassMethod();
        }

        super.visitEnd();
    }

    private void createFinalizeMethod() {
        InstructionAdapter i = new InstructionAdapter(
                super.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        "finalize",
                        Type.getMethodDescriptor(Type.VOID_TYPE),
                        null,
                        new String[] { ClassNameUtils.toInternalName(Throwable.class.getName()) }
                )
        );

        i.load(0, Type.getObjectType(ClassNameUtils.toInternalName(className)));
        i.invokestatic(
                ClassNameUtils.toInternalName(MemoryUtils.class.getName()),
                "freeInstance",
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getObjectType(ClassNameUtils.toInternalName(__PuccjObject.class.getName()))),
                false
        );
        i.areturn(Type.VOID_TYPE);
        i.visitMaxs(1, 1);
        i.visitEnd();
    }

    private void createGetInstanceIdMethod() {
        InstructionAdapter i = new InstructionAdapter(
                super.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        Methods.INSTANCE_ID_GETTER,
                        Type.getMethodDescriptor(Type.LONG_TYPE),
                        null,
                        null
                )
        );

        i.load(0, Type.getObjectType(ClassNameUtils.toInternalName(className)));
        i.getfield(ClassNameUtils.toInternalName(className), Methods.INSTANCE_ID_FIELD, Type.LONG_TYPE.getDescriptor());
        i.areturn(Type.LONG_TYPE);
        i.visitMaxs(2, 1);
        i.visitEnd();
    }

    private void createSetInstanceIdMethod() {
        InstructionAdapter i = new InstructionAdapter(
                super.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        Methods.INSTANCE_ID_SETTER,
                        Type.getMethodDescriptor(Type.VOID_TYPE, Type.LONG_TYPE),
                        null,
                        null
                )
        );

        i.load(0, Type.getObjectType(ClassNameUtils.toInternalName(className)));
        i.load(1, Type.LONG_TYPE);
        i.putfield(ClassNameUtils.toInternalName(className), Methods.INSTANCE_ID_FIELD, Type.LONG_TYPE.getDescriptor());
        i.areturn(Type.VOID_TYPE);
        i.visitMaxs(2, 2);
        i.visitEnd();
    }

    private void createPassMethod() {
        InstructionAdapter i = new InstructionAdapter(
                super.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        Methods.PASS,
                        Type.getMethodDescriptor(Type.VOID_TYPE),
                        null,
                        null
                )
        );

        i.areturn(Type.VOID_TYPE);
        i.visitMaxs(1, 1);
        i.visitEnd();
    }

    private void createInstanceIdField() {
        super.visitField(Opcodes.ACC_PUBLIC, Methods.INSTANCE_ID_FIELD, Type.LONG_TYPE.getDescriptor(), null, null);
    }
}


