package applica.puccj.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by bimbobruno on 07/10/15.
 */
public class TrashClassVisitor extends ClassVisitor {

    public static final String TRASH_SUFFIX = "__puccj_trash";

    public TrashClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name + TRASH_SUFFIX, signature, superName, interfaces);
    }

}


