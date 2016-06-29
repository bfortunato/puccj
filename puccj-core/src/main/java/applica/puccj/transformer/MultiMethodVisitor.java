package applica.puccj.transformer;

import org.objectweb.asm.*;

import java.util.HashMap;

/**
 * Created by bimbobruno on 07/11/15.
 */
public class MultiMethodVisitor extends MethodVisitor {


    private HashMap<Label, HashMap<MethodVisitor, Label>> labelsMap = new HashMap<>();
    private final MethodVisitor[] children;

    private Label getLabel(Label original, MethodVisitor mv) {
        HashMap<MethodVisitor, Label> map = null;
        Label label = null;

        if (labelsMap.containsKey(original)) {
            map = labelsMap.get(original);
        } else {
            map = new HashMap<>();
            labelsMap.put(original, map);
            label = original;
        }

        if (label == null) {
            if (map.containsKey(mv)) {
                label = map.get(mv);
            } else {
                label = new Label();
                map.put(mv, label);
            }
        }

        return label;
    }

    public MultiMethodVisitor(MethodVisitor... children) {
        super(Opcodes.ASM5);
        
        this.children = children;
    }

    public void visitParameter(String name, int access) {
        for (MethodVisitor mv : children) mv.visitParameter(name, access);
    }

    public AnnotationVisitor visitAnnotationDefault() {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            ans[i++] = mv.visitAnnotationDefault();
        }

        return new MultiAnnotationVisitor(ans);
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            ans[i++] = mv.visitAnnotation(desc, visible);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            ans[i++] = mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            ans[i++] = mv.visitParameterAnnotation(parameter, desc, visible);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public void visitAttribute(Attribute attr) {
        for (MethodVisitor mv : children) mv.visitAttribute(attr);
    }

    public void visitCode() {
        for (MethodVisitor mv : children) mv.visitCode();
    }

    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        for (MethodVisitor mv : children) mv.visitFrame(type, nLocal, local, nStack, stack);
    }

    public void visitInsn(int opcode) {
        for (MethodVisitor mv : children) mv.visitInsn(opcode);
    }

    public void visitIntInsn(int opcode, int operand) {
        for (MethodVisitor mv : children) mv.visitIntInsn(opcode, operand);
    }

    public void visitVarInsn(int opcode, int var) {
        for (MethodVisitor mv : children) mv.visitVarInsn(opcode, var);
    }

    public void visitTypeInsn(int opcode, String type) {
        for (MethodVisitor mv : children) mv.visitTypeInsn(opcode, type);
    }

    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        for (MethodVisitor mv : children) mv.visitFieldInsn(opcode, owner, name, desc);
    }

    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        for (MethodVisitor mv : children) mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        for (MethodVisitor mv : children) mv.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    public void visitJumpInsn(int opcode, Label label) {
        for (MethodVisitor mv : children) { mv.visitJumpInsn(opcode, getLabel(label, mv)); }
    }

    public void visitLabel(Label label) {
        for (MethodVisitor mv : children) {
            mv.visitLabel(getLabel(label, mv));
        }
    }

    public void visitLdcInsn(Object cst) {
        for (MethodVisitor mv : children) mv.visitLdcInsn(cst);
    }

    public void visitIincInsn(int var, int increment) {
        for (MethodVisitor mv : children) mv.visitIincInsn(var, increment);
    }

    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        for (MethodVisitor mv : children) {
            Label[] labelsCopy = new Label[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labelsCopy[i] = getLabel(labels[i], mv);
            }
            mv.visitTableSwitchInsn(min, max, getLabel(dflt, mv), labelsCopy);
        }
    }

    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        for (MethodVisitor mv : children) {
            Label[] labelsCopy = new Label[labels.length];
            for (int i = 0; i < labels.length; i++) {
                labelsCopy[i] = getLabel(labels[i], mv);
            }
            mv.visitLookupSwitchInsn(getLabel(dflt, mv), keys, labelsCopy);
        }
    }

    public void visitMultiANewArrayInsn(String desc, int dims) {
        for (MethodVisitor mv : children) mv.visitMultiANewArrayInsn(desc, dims);
    }

    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            ans[i++] = mv.visitInsnAnnotation(typeRef, typePath, desc, visible);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        for (MethodVisitor mv : children) {
            mv.visitTryCatchBlock(getLabel(start, mv), getLabel(end, mv), getLabel(handler, mv), type);
        }
    }

    public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            ans[i++] = mv.visitTryCatchAnnotation(typeRef, typePath, desc, visible);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        for (MethodVisitor mv : children) mv.visitLocalVariable(name, desc, signature, getLabel(start, mv), getLabel(end, mv), index);
    }

    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String desc, boolean visible) {
        AnnotationVisitor[] ans = new AnnotationVisitor[children.length];
        int i = 0;
        for (MethodVisitor mv : children) {
            Label[] labelsCopyStart = new Label[start.length];
            for (int li = 0; li < start.length; li++) {
                labelsCopyStart[i] = getLabel(start[i], mv);
            }

            Label[] labelsCopyEnd = new Label[end.length];
            for (int li = 0; li < end.length; li++) {
                labelsCopyEnd[i] = getLabel(end[i], mv);
            }
            ans[i++] = mv.visitLocalVariableAnnotation(typeRef, typePath, labelsCopyStart, labelsCopyEnd, index, desc, visible);
        }

        return new MultiAnnotationVisitor(ans);
    }

    public void visitLineNumber(int line, Label start) {
        for (MethodVisitor mv : children) mv.visitLineNumber(line, getLabel(start, mv));
    }

    public void visitMaxs(int maxStack, int maxLocals) {
        for (MethodVisitor mv : children) mv.visitMaxs(maxStack, maxLocals);
    }

    public void visitEnd() {
        for (MethodVisitor mv : children) mv.visitEnd();
    }
}
