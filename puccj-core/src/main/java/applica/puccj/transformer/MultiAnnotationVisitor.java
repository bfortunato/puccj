package applica.puccj.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by bimbobruno on 07/11/15.
 */
public class MultiAnnotationVisitor extends AnnotationVisitor {


    private final AnnotationVisitor[] children;

    public MultiAnnotationVisitor(AnnotationVisitor... children) {
        super(Opcodes.ASM5);

        this.children = children;
    }

    public void visit(String name, Object value) {
        for (AnnotationVisitor av : children) av.visit(name, value);
    }

    public void visitEnum(String name, String desc, String value) {
        for (AnnotationVisitor av : children) av.visitEnum(name, desc, value);
    }

    public AnnotationVisitor visitAnnotation(String name, String desc) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int index = 0;
        for (AnnotationVisitor av : children) {
            ans[index++] = av.visitAnnotation(name, desc);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public AnnotationVisitor visitArray(String name) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int index = 0;
        for (AnnotationVisitor av : children) {
            ans[index++] = av.visitArray(name);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public void visitEnd() {
        for (AnnotationVisitor av : children) av.visitEnd();
    }
}
