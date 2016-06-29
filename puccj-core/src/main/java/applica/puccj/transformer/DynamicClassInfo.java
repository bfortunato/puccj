package applica.puccj.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class DynamicClassInfo extends ClassVisitor {

    private String name;

    public class MethodInfo {
        private String name;
        private String desc;

        public MethodInfo(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    private boolean interf4ce;
    private String superName;
    private String signature;
    private String[] interfaces;
    private int access;
    private boolean publicClass;

    private List<MethodInfo> methods = new ArrayList<>();

    public DynamicClassInfo(byte[] bytes) {
        super(Opcodes.ASM5);

        ClassReader cr = new ClassReader(bytes);
        cr.accept(this, 0);
    }

    public DynamicClassInfo(ClassReader cr) {
        super(Opcodes.ASM5);

        cr.accept(this, 0);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        interf4ce = (access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;
        this.name = name;
        this.signature = signature;
        this.interfaces = interfaces;
        this.superName = superName;
        this.access = access;
        this.publicClass =(access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC;


    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        methods.add(new MethodInfo(name, desc));

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public boolean isInterf4ce() {
        return interf4ce;
    }

    public String getSuperName() {
        return superName;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getInterfaces() {
        return interfaces;
    }

    public int getAccess() {
        return access;
    }

    public boolean isPublicClass() {
        return publicClass;
    }

    public List<MethodInfo> getMethods() {
        return methods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMethodDefined(String name, String desc) {
        for (MethodInfo m : methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return true;
            }
        }

        return false;
    }
}
