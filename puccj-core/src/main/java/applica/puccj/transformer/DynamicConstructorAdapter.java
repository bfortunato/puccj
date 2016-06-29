package applica.puccj.transformer;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.InstructionAdapter;

import java.util.List;

/**
 * Created by bimbobruno on 15/10/15.
 */
public class DynamicConstructorAdapter extends InstructionAdapter {

    private final String className;
    private final String superName;
    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final String[] exceptions;
    private final List<String> allowedPackages;
    private boolean notInCode = false;
    private int labelIndex = 0;
    private boolean returned;

    public DynamicConstructorAdapter(String className, String superName, int access, String name, String desc, String signature, String[] exceptions, List<String> allowedPackages, MethodVisitor mv) {
        super(Opcodes.ASM5, mv);

        this.className = className;
        this.superName = superName;
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
        this.allowedPackages = allowedPackages;

    }

    @Override
    public void visitLabel(Label label) {
        labelIndex++;
        notInCode = labelIndex <= 1;

        if (!notInCode) {
            super.visitLabel(label);
        }
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        if (notInCode) {
            return;
        }

        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(int opcode) {
        if (Opcodes.RETURN == opcode) {
            super.visitInsn(opcode);
            returned = true;
            return;
        }

        if (notInCode) {
            return;
        }

        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        if (notInCode) {
            return;
        }

        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        if (notInCode) {
            return;
        }

        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        if (notInCode) {
            return;
        }

        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (notInCode) {
            return;
        }

        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        if (notInCode) {
            return;
        }

        if (owner.equals(superName) && name.equals("<init>")) {
            notInCode = false;
            return;
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        if (notInCode) {
            return;
        }

        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        if (notInCode) {
            return;
        }

        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLdcInsn(Object cst) {
        if (notInCode) {
            return;
        }
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        if (notInCode) {
            return;
        }

        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        if (notInCode) {
            return;
        }

        super.visitTableSwitchInsn(min, max, dflt, labels);
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        if (notInCode) {
            return;
        }

        super.visitLookupSwitchInsn(dflt, keys, labels);
    }

    @Override
    public void visitMultiANewArrayInsn(String desc, int dims) {
        if (notInCode) {
            return;
        }

        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        if (notInCode) {
            return;
        }

        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        if (notInCode) {
            return;
        }

        super.visitLineNumber(line, start);
    }

    @Override
    public void visitEnd() {
        if (!returned) {
            areturn(Type.VOID_TYPE);
        }

        super.visitEnd();
    }
}
