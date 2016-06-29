package applica.puccj.linkage;

import applica.puccj.linkage.arguments.*;
import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.__PuccjObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.invoke.*;

/**
 * Created by bimbobruno on 10/10/15.
 */
public class Linkage {

    private static Log logger = LogFactory.getLog(Linkage.class);

    public static Object invokeVirtual(Object... args) throws Throwable {
        InvokeVirtualArguments va = new InvokeVirtualArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        __PuccjObject runtimeInstance = ((__PuccjObject) va.getInstance());

        return DynamicRuntime.instance().getCode()
                .virtual(type)
                .pointer(va.getName(), va.getDesc())
                .invoke(runtimeInstance, va.getArgs());
    }

    public static Object invokeStatic(Object... args) throws Throwable {
        InvokeStaticArguments va = new InvokeStaticArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());

        Object returnValue = DynamicRuntime.instance().getCode()
                .statik(type)
                .pointer(va.getName(), va.getDesc())
                .invokestatic(va.getArgs());

        return checkedReturnValue(returnValue);
    }

    public static Object invokeSpecial(Object... args) throws Throwable {
        InvokeSpecialArguments va = new InvokeSpecialArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        __PuccjObject runtimeInstance = ((__PuccjObject) va.getInstance());
        return DynamicRuntime.instance().getCode()
                .special(type)
                .pointer(va.getName(), va.getDesc())
                .invoke(runtimeInstance, va.getArgs());

    }

    public static Object invokeInterface(Object... args) throws Throwable {
        InvokeInterfaceArguments va = new InvokeInterfaceArguments(args);

        RuntimeType type = new RuntimeType(va.getOwner());
        __PuccjObject runtimeInstance = ((__PuccjObject) va.getInstance());
        return DynamicRuntime.instance().getCode()
                .interfac3(type)
                .pointer(va.getName(), va.getDesc())
                .invoke(runtimeInstance, va.getArgs());

    }

    public static void invokeConstructor(Object... args) throws Throwable {
        InvokeSpecialArguments va = new InvokeSpecialArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        __PuccjObject runtimeInstance = ((__PuccjObject) va.getInstance());
        DynamicRuntime.instance().getCode()
                .constructor(type)
                .pointer(va.getDesc())
                .invoke(runtimeInstance, va.getArgs());

    }

    public static Object invokeGetField(Object... args) throws Throwable {
        GetFieldArguments va = new GetFieldArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        __PuccjObject runtimeInstance = ((__PuccjObject) va.getInstance());
        return DynamicRuntime.instance().getCode()
                .getField(type)
                .pointer(va.getName(), va.getDesc())
                .get(runtimeInstance);
    }

    public static void invokePutField(Object... args) throws Throwable {
        PutFieldArguments va = new PutFieldArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        __PuccjObject runtimeInstance = ((__PuccjObject) va.getInstance());
        DynamicRuntime.instance().getCode()
                .putField(type)
                .pointer(va.getName(), va.getDesc())
                .put(runtimeInstance, va.getVal());
    }

    public static Object invokeGetStatic(Object... args) throws Throwable {
        GetStaticArguments va = new GetStaticArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        return DynamicRuntime.instance().getCode()
                .getStatic(type)
                .pointer(va.getName(), va.getDesc())
                .get();
    }

    public static void invokePutStatic(Object... args) throws Throwable {
        PutStaticArguments va = new PutStaticArguments(args);
        RuntimeType type = new RuntimeType(va.getOwner());
        DynamicRuntime.instance().getCode()
                .putStatic(type)
                .pointer(va.getName(), va.getDesc())
                .put(va.getVal());
    }

    private static Object checkedReturnValue(Object returnValue) {
        return returnValue;

        /*
        if (returnValue == null) {
            return null;
        }

        Class rt = returnValue.getClass();

        if (DynamicRuntime.instance().isPackageAllowed(rt.getName())) {
            if (!ClassesRegistry.instance().isLatestVersionOfClass(rt) && !DynamicRuntime.instance().getMemory().isRuntimeInstance(returnValue)) {
                RuntimeType runtimeType = new RuntimeType(ClassNameUtils.toInternalName(rt.getName()));
                RuntimeInstance newInstance = DynamicRuntime.instance().getMemory().newRuntimeInstance(runtimeType, returnValue);

                logger.debug(String.format("return value %s replaced after check with new value: %s", returnValue, newInstance.toString()));

                returnValue = newInstance.getInstanceId();
            }
        }

        return returnValue;

        */
    }

    /* bootstrap */

    public static CallSite bootstrapVirtual(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        logger.debug(String.format("Bootstrapping virtual %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeVirtual", MethodType.methodType(Object.class, Object[].class));
        MethodHandle adaptedTarget = target.asType(methodType);

        logger.debug(String.format("Created method handle %s", adaptedTarget));

        return new ConstantCallSite(adaptedTarget);
    }

    public static CallSite bootstrapSpecial(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        logger.debug(String.format("Bootstrapping special %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeSpecial", MethodType.methodType(Object.class, Object[].class));
        MethodHandle adaptedTarget = target.asType(methodType);

        logger.debug(String.format("Created method handle %s", adaptedTarget));

        return new ConstantCallSite(adaptedTarget);
    }

    public static CallSite bootstrapInterface(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        logger.debug(String.format("Bootstrapping interface %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeInterface", MethodType.methodType(Object.class, Object[].class));
        MethodHandle adaptedTarget = target.asType(methodType);

        logger.debug(String.format("Created method handle %s", adaptedTarget));

        return new ConstantCallSite(adaptedTarget);
    }

    public static CallSite bootstrapConstructor(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        logger.debug(String.format("Bootstrapping constructor %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeConstructor", MethodType.methodType(void.class, Object[].class));
        MethodHandle adaptedTarget = target.asType(methodType);

        logger.debug(String.format("Created method handle %s", adaptedTarget));

        return new ConstantCallSite(adaptedTarget);
    }

    public static CallSite bootstrapStatic(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException {
        logger.debug(String.format("Bootstrapping static %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeStatic", MethodType.methodType(Object.class, Object[].class));
        MethodHandle adaptedTarget = target.asType(methodType);

        logger.debug(String.format("Created method handle %s", adaptedTarget));

        return new ConstantCallSite(adaptedTarget);
    }

    public static CallSite bootstrapGetField(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        logger.debug(String.format("Bootstrapping getField %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeGetField", MethodType.methodType(Object.class, Object[].class));

        logger.debug(String.format("Created method handle %s", target));

        return new ConstantCallSite(target.asType(methodType));
    }

    public static CallSite bootstrapPutField(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        logger.debug(String.format("Bootstrapping putField %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokePutField", MethodType.methodType(void.class, Object[].class));

        logger.debug(String.format("Created method handle %s", target));

        return new ConstantCallSite(target.asType(methodType));
    }

    public static CallSite bootstrapGetStatic(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        logger.debug(String.format("Bootstrapping getStatic %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokeGetStatic", MethodType.methodType(Object.class, Object[].class));

        logger.debug(String.format("Created method handle %s", target));

        return new ConstantCallSite(target.asType(methodType));
    }

    public static CallSite bootstrapPutStatic(MethodHandles.Lookup caller, String name, MethodType methodType) throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException {
        logger.debug(String.format("Bootstrapping putStatic %s.%s%s", caller, name, methodType));

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle target = lookup.findStatic(Linkage.class, "invokePutStatic", MethodType.methodType(void.class, Object[].class));

        logger.debug(String.format("Created method handle %s", target));

        return new ConstantCallSite(target.asType(methodType));
    }

}
