package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.RuntimeField;
import applica.puccj.runtime.__PuccjObject;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandle;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class PutStaticPointer extends Pointer {

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

    public void put(Object val) {
        String className = Type.getMethodType(getDesc()).getClassName();

        DynamicRuntime.instance().getMemory().ofClass(getOwner()).put(new RuntimeField(getName(), getOwner()), val);
    }
}
