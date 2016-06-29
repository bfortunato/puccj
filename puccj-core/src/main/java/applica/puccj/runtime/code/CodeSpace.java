package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.Pointer;
import applica.puccj.utils.ClassNameUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class CodeSpace {

    private List<VirtualCode> virtualCodes = new ArrayList<>();
    private List<SpecialCode> specialCodes = new ArrayList<>();
    private List<InterfaceCode> interfaceCodes = new ArrayList<>();
    private List<ConstructorCode> constructorCodes = new ArrayList<>();
    private List<StaticCode> staticCodes = new ArrayList<>();
    private List<GetFieldCode> getFieldCodes = new ArrayList<>();
    private List<PutFieldCode> putFieldCodes = new ArrayList<>();
    private List<PutStaticCode> putStaticCodes = new ArrayList<>();
    private List<GetStaticCode> getStaticCodes = new ArrayList<>();

    private <T extends Code> T findCode(List<T> codes, final RuntimeType type) {
        T code = (T) CollectionUtils.find(codes, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                Code code = ((Code) o);
                return code.getType().equals(type);
            }
        });

        return code;
    }

    public VirtualCode virtual(RuntimeType type) {
        VirtualCode code = findCode(virtualCodes, type);

        if (code == null) {
            code = new VirtualCode(type);
            virtualCodes.add(code);
        }

        return code;
    }

    public SpecialCode special(RuntimeType type) {
        SpecialCode code = findCode(specialCodes, type);
        if (code == null) {
            code = new SpecialCode(type);
            specialCodes.add(code);
        }

        return code;
    }

    public InterfaceCode interfac3(RuntimeType type) {
        InterfaceCode code = findCode(interfaceCodes, type);
        if (code == null) {
            code = new InterfaceCode(type);
            interfaceCodes.add(code);
        }

        return code;
    }

    public ConstructorCode constructor(RuntimeType type) {
        ConstructorCode code = findCode(constructorCodes, type);
        if (code == null) {
            code = new ConstructorCode(type);
            constructorCodes.add(code);
        }

        return code;
    }

    public StaticCode statik(RuntimeType type) {
        StaticCode code = findCode(staticCodes, type);
        if (code == null) {
            code = new StaticCode(type);
            staticCodes.add(code);
        }

        return code;
    }

    public GetFieldCode getField(RuntimeType type) {
        GetFieldCode code = findCode(getFieldCodes, type);
        if (code == null) {
            code = new GetFieldCode(type);
            getFieldCodes.add(code);
        }

        return code;
    }

    public PutFieldCode putField(RuntimeType type) {
        PutFieldCode code = findCode(putFieldCodes, type);
        if (code == null) {
            code = new PutFieldCode(type);
            putFieldCodes.add(code);
        }

        return code;
    }

    public GetStaticCode getStatic(RuntimeType type) {
        GetStaticCode code = findCode(getStaticCodes, type);
        if (code == null) {
            code = new GetStaticCode(type);
            getStaticCodes.add(code);
        }

        return code;
    }

    public PutStaticCode putStatic(RuntimeType type) {
        PutStaticCode code = findCode(putStaticCodes, type);
        if (code == null) {
            code = new PutStaticCode(type);
            putStaticCodes.add(code);
        }

        return code;
    }

    public void invalidateClass(String classInternalName) {
        RuntimeType type = new RuntimeType(classInternalName);
        virtual(type).invalidate();
        special(type).invalidate();
        interfac3(type).invalidate();
        statik(type).invalidate();
        getField(type).invalidate();
        putField(type).invalidate();
        getStatic(type).invalidate();
        putStatic(type).invalidate();
        constructor(type).invalidate();

        //clean interfaces code
        String className = ClassNameUtils.toJavaName(classInternalName);
        for (InterfaceCode interfaceCode : interfaceCodes) {
            for (Pointer p : interfaceCode.getPointers()) {
                if (p.isLookupClassEquals(className)) {
                    p.invalidate();
                }
            }
        }
    }
}
