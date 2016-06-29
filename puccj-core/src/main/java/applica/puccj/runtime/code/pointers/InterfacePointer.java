package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.__PuccjObject;
import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.utils.TypeUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class InterfacePointer extends Pointer {


    @Override
    protected MethodHandle createMethodHandle(__PuccjObject instance) {
        Class clazz = null;
        try {
            clazz = ClassesRegistry.instance().loadClass(instance.getClass().getName(), instance);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        MethodHandles.Lookup lookup = getLookup(clazz, instance);
        try {
            MethodType mt = MethodType.fromMethodDescriptorString(getDesc(), clazz.getClassLoader());
            MethodHandle target = null;

            if (INIT_NAME.equals(getName())) {
                target = lookup.findConstructor(clazz, mt);
            } else {
                target = lookup.findSpecial(clazz, getName(), mt, lookup.lookupClass());
            }

            return target;
        } catch (Exception e) {
            if (CLASS_DUMP_ON_ERROR) {
                System.err.println("Class dump");
                TypeUtils.dumpClass(clazz, System.err);

                System.err.println("Memory dump");
                DynamicRuntime.instance().getMemory().ofInstance(instance.__puccj_getInstanceId()).dump(System.err);
            }

            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
