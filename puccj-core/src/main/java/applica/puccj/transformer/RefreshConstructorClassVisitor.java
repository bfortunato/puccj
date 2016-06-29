package applica.puccj.transformer;

import applica.puccj.runtime.classes.RefreshConstructorParameter;
import applica.puccj.utils.ClassNameUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 19/01/16.
 */
public class RefreshConstructorClassVisitor extends ClassVisitor {
    private String className;
    private List<String> allowedPackages;
    private String superName;
    private DynamicClassInfo classInfo;

    public RefreshConstructorClassVisitor(String className, List<String> allowedPackages, ClassVisitor cv, DynamicClassInfo classInfo) {
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
        this.superName = superName;

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        createRefreshConstructor();

        super.visitEnd();
    }

    private void createRefreshConstructor() {
        try {
            MethodType mt = MethodType.methodType(void.class, RefreshConstructorParameter.class);
            InstructionAdapter i = new InstructionAdapter(cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", mt.toMethodDescriptorString(), null, null));

            i.visitCode();
            i.load(0, Type.getObjectType(ClassNameUtils.toInternalName(className)));

            if (superName.startsWith("java/lang")) {
                i.invokespecial(superName, "<init>", "()V", false);
            } else {
                i.load(1, Type.getObjectType(ClassNameUtils.toInternalName(RefreshConstructorParameter.class.getName())));
                i.invokespecial(superName, "<init>", mt.toMethodDescriptorString(), false);
            }

            i.visitInsn(Opcodes.RETURN);
            i.visitMaxs(2, 2);
            i.visitEnd();
        } catch (Exception e) {
            throw new RuntimeException("Error creating refresh constructor: " + className, e);
        }
    }
}
