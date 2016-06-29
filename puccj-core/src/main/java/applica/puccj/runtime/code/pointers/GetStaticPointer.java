package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.RuntimeField;
import applica.puccj.runtime.__PuccjObject;
import applica.puccj.utils.TypeUtils;

import java.lang.invoke.MethodHandle;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class GetStaticPointer extends Pointer {

    @Override
    protected boolean hasReturnType() {
        return false;
    }

    @Override
    protected MethodHandle createMethodHandle(__PuccjObject instance) {
        throw new MethodNotSupportedException();
    }

    @Override
    public Object invokestatic(Object... args) throws Throwable {
        throw new MethodNotSupportedException();
    }

    @Override
    public Object invoke(__PuccjObject runtimeInstance, Object... args) throws Throwable {
        throw new MethodNotSupportedException();
    }

    public Object get() {
        Object value = DynamicRuntime.instance().getMemory().ofClass(getOwner()).get(new RuntimeField(getName(), getOwner()));

        if (value == null && TypeUtils.isPrimitive(desc)) {
            return TypeUtils.getPrimitiveDefaultType(desc);
        }

        return value;
    }
}
