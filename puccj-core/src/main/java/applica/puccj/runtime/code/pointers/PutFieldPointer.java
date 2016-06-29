package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.RuntimeField;
import applica.puccj.runtime.__PuccjObject;

import java.lang.invoke.MethodHandle;
import java.util.Objects;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class PutFieldPointer extends Pointer {

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
    public Object invoke(__PuccjObject instance, Object... args) throws Throwable {
        throw new MethodNotSupportedException();
    }

    public void put(__PuccjObject instance, Object val) {
        Objects.requireNonNull(instance);
        DynamicRuntime.instance().getMemory().ofInstance(instance.__puccj_getInstanceId()).put(new RuntimeField(getName(), getOwner()), val);
    }


}
