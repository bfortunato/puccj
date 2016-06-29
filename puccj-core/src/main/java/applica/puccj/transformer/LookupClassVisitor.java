package applica.puccj.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;

/**
 * Created by bimbobruno on 15/10/15.
 */
public class LookupClassVisitor extends ClassVisitor {

    public LookupClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public void visitEnd() {
        ClassWriter cw = (ClassWriter) cv;

        //lookup method for static classes
        InstructionAdapter ia = new InstructionAdapter(
                cw.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        Methods.LOOKUP,
                        Type.getMethodType(Type.getObjectType("java/lang/invoke/MethodHandles$Lookup")).getInternalName(),
                        null,
                        null
                )
        );

        ia.visitCode();
        ia.invokestatic("java/lang/invoke/MethodHandles", "lookup", Type.getMethodType(Type.getObjectType("java/lang/invoke/MethodHandles$Lookup")).getInternalName(), false);
        ia.areturn(Type.getObjectType("java/lang/invoke/MethodHandles$Lookup"));
        ia.visitMaxs(1, 0);
        ia.visitEnd();

        super.visitEnd();
    }
}
