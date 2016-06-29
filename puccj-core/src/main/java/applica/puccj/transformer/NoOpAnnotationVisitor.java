package applica.puccj.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by bimbobruno on 18/02/16.
 */
public class NoOpAnnotationVisitor extends AnnotationVisitor {
    public NoOpAnnotationVisitor() {
        super(Opcodes.ASM5);
    }

    @Override
    public void visit(String name, Object value) {

    }

    @Override
    public void visitEnum(String name, String desc, String value) {

    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return new NoOpAnnotationVisitor();
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new NoOpAnnotationVisitor();
    }

    @Override
    public void visitEnd() {

    }
}
