package applica.puccj.transformer;

import applica.puccj.utils.TypeUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureWriter;

import java.util.List;

/**
 * Created by bimbobruno on 22/10/15.
 */
public class DynamicSignatureWriter extends SignatureWriter {

    private final List<String> allowedPackages;

    public DynamicSignatureWriter(List<String> allowedPackages) {
        this.allowedPackages = allowedPackages;
    }

    @Override
    public void visitInnerClassType(String name) {
        super.visitInnerClassType(TypeUtils.checkedType(allowedPackages, Type.getObjectType(name)).getInternalName());
    }

    @Override
    public void visitTypeVariable(String name) {
        super.visitTypeVariable(TypeUtils.checkedType(allowedPackages, Type.getObjectType(name)).getInternalName());
    }

    @Override
    public void visitClassType(String name) {
        super.visitClassType(TypeUtils.checkedType(allowedPackages, Type.getObjectType(name)).getInternalName());
    }

}
