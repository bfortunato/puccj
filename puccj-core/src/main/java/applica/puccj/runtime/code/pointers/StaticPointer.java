package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.__PuccjObject;
import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.utils.TypeUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class StaticPointer extends Pointer {

    @Override
    public Object invoke(__PuccjObject instance, Object... args) throws Throwable {
        throw new MethodNotSupportedException();
    }


    @Override
    protected MethodHandle createMethodHandle(__PuccjObject instance) {
        Class clazz = null;
        try {
            clazz = ClassesRegistry.instance().loadClass(getOwner().getClassInternalName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        MethodHandles.Lookup lookup = getLookup(clazz, instance);
        try {
            MethodType mt = MethodType.fromMethodDescriptorString(getDesc(), clazz.getClassLoader());
            MethodHandle target = lookup.findStatic(clazz, getName(), mt);

            return target;
        } catch (Exception e) {
            e.printStackTrace();
            if (CLASS_DUMP_ON_ERROR) {
                System.err.println("Class dump");
                TypeUtils.dumpClass(clazz, System.err);
            }

            throw new RuntimeException(e);
        }
    }
}
