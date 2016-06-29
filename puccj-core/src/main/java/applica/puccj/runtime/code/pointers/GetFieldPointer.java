package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.RuntimeField;
import applica.puccj.runtime.__PuccjObject;
import applica.puccj.runtime.memory.Memory;
import applica.puccj.runtime.memory.Value;
import applica.puccj.utils.TypeUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Created by bimbobruno on 14/10/15.
 */
public class GetFieldPointer extends Pointer {

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

    public Object get(__PuccjObject instance) {
        Objects.requireNonNull(instance);

        RuntimeField field = new RuntimeField(getName(), getOwner());
        Memory memory = DynamicRuntime.instance().getMemory().ofInstance(instance.__puccj_getInstanceId());
        Object val = null;
        Value value = memory.getValue(field);
        if (value != null) {
            val = value.getVal();
        } else {
            //there is the possibility that this field was setted outside the a dynamic class through reflection
            Field reflectedField = null;
            try {
                reflectedField = instance.getClass().getDeclaredField(getName());
                reflectedField.setAccessible(true);
                val = reflectedField.get(instance);

                if (val != null) {
                    memory.put(field, val);
                }

                logger.debug(String.format("Reflected field %s.%s value: %s", instance.getClass().getName(), getName(), val));
            } catch (Exception e) {
                logger.warn(String.format("Error trying to access field %s.%s", instance.getClass().getName(), getName()));
                if (CLASS_DUMP_ON_ERROR) {
                    System.err.println("Class dump");
                    TypeUtils.dumpClass(instance.getClass(), System.err);

                    System.err.println("Memory dump");
                    DynamicRuntime.instance().getMemory().ofInstance(instance.__puccj_getInstanceId()).dump(System.err);
                }
            }

        }

        if (val == null && TypeUtils.isPrimitive(desc)) {
            return TypeUtils.getPrimitiveDefaultType(desc);
        }

        return val;
    }
}
