package applica.puccj.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

/**
 * Created by bimbobruno on 15/10/15.
 */
public class ClassNameClassVisitor extends ClassVisitor {

    private final String className;

    public ClassNameClassVisitor(String className, ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    @Override
    public void visitEnd() {
        ClassWriter cw = (ClassWriter) cv;
        InstructionAdapter ia = new InstructionAdapter(
                cw.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        Methods.CLASSNAME,
                        Type.getMethodType(Type.getObjectType("java/lang/String")).getInternalName(),
                        null,
                        null
                )
        );

        ia.visitCode();
        ia.aconst(className);
        ia.areturn(Type.getObjectType("java/lang/String"));
        ia.visitMaxs(1, 0);
        ia.visitEnd();

        super.visitEnd();
    }
}
