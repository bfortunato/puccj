package applica.puccj.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

/**
 * Created by bimbobruno on 18/02/16.
 */
public class RemoveAnnotationsMethodVisitor extends MethodVisitor {

    public RemoveAnnotationsMethodVisitor(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);

    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return super.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return new NoOpAnnotationVisitor();
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        return new NoOpAnnotationVisitor();
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        return new NoOpAnnotationVisitor();
    }


}
