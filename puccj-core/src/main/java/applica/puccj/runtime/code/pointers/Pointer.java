package applica.puccj.runtime.code.pointers;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.__PuccjObject;
import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.transformer.Methods;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Created by bimbobruno on 11/10/15.
 */
public abstract class Pointer {

    public static boolean CLASS_DUMP_ON_ERROR = false;

    public static final String INIT_NAME = "<init>";

    protected RuntimeType owner;
    protected String name;
    protected String desc;
    protected Log logger = LogFactory.getLog(getClass());
    private MethodHandles.Lookup targetLookup;

    protected MethodHandle methodHandle;

    public RuntimeType getOwner() {
        return owner;
    }

    public void setOwner(RuntimeType owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public MethodHandle getMethodHandle() {
        return methodHandle;
    }

    public void setMethodHandle(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    public Object invoke(__PuccjObject instance, Object... args) throws Throwable {
        Object[] iargs = new Object[1 + args.length];
        int i = 0;
        __PuccjObject latestInstance = DynamicRuntime.instance().invokableInstance(getOwner(), instance);
        iargs[i++] = latestInstance;
        for (Object arg : args) {
            iargs[i++] = arg;
        }

        if (methodHandle == null) {
            methodHandle = createMethodHandle(latestInstance);
        }

        logger.debug(String.format("Invoking %s.%s %s", owner, name, desc));

        try {
            if (hasReturnType()) {
                return methodHandle.invokeWithArguments(iargs);
            } else {
                methodHandle.invokeWithArguments(iargs);
                return null;
            }
        } catch (Throwable t) {
            if (CLASS_DUMP_ON_ERROR) {
                System.err.println("Class dump");
                TypeUtils.dumpClass(instance.getClass(), System.err);

                System.err.println("Memory dump");
                DynamicRuntime.instance().getMemory().ofInstance(instance.__puccj_getInstanceId()).dump(System.err);
            }

            throw t;
        }
    }

    public Object invokestatic(Object... args) throws Throwable {
        if (methodHandle == null) {
            methodHandle = createMethodHandle(null);
        }

        logger.debug(String.format("Invoking static %s.%s %s", owner, name, desc));

        if (hasReturnType()) {
            return methodHandle.invokeWithArguments(args);
        } else {
            methodHandle.invokeWithArguments(args);
            return null;
        }
    }

    protected boolean hasReturnType() {
        return true;
    }

    protected abstract MethodHandle createMethodHandle(__PuccjObject instance);

    public void invalidate() {
        logger.debug(String.format("%s.%s %s invalidated", owner, name, desc));
        methodHandle = null;
        targetLookup = null;
    }

    protected MethodHandles.Lookup getLookup(Class clazz, __PuccjObject instance) {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            if (targetLookup == null) {
                String className = clazz.getName();

                if (clazz.getEnclosingClass() != null) {
                    className = clazz.getEnclosingClass().getName();
                }

                Class latest;
                if (instance != null) { latest = ClassesRegistry.instance().loadClass(className, instance); }
                else { latest = ClassesRegistry.instance().loadClass(className); }

                MethodHandle mh = lookup.findStatic(latest, Methods.LOOKUP, MethodType.methodType(MethodHandles.Lookup.class));
                targetLookup = ((MethodHandles.Lookup) mh.invoke());
            }

            return targetLookup;
        } catch (Throwable e) {
            throw new RuntimeException("Cannot lookup static class: " + clazz.getName(), e);
        }
    }

    public boolean isLookupClassEquals(String className) {
        if (targetLookup != null) {
            return targetLookup.lookupClass().getName().equals(className);
        }

        return false;
    }
}
