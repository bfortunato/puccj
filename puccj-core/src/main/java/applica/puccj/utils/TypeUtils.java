package applica.puccj.utils;

import applica.puccj.runtime.__PuccjObject;
import applica.puccj.transformer.DynamicClassInfo;
import applica.puccj.transformer.DynamicSignatureWriter;
import applica.puccj.transformer.SuperType;
import applica.puccj.transformer.SuperTypesRegistry;
import org.apache.commons.io.FilenameUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by bimbobruno on 20/10/15.
 */
public class TypeUtils {

    private static Method defineMethod;

    public static boolean isPackageAllowed(List<String> packages, String name) {
        String internalName = ClassNameUtils.toInternalName(name);
        for (String pack : packages) {
            String internalPack = ClassNameUtils.toInternalName(pack);
            if (internalName.startsWith(internalPack)) {
                return true;
            }
        }

        return false;
    }

    public static Type checkedType(List<String> packages, Type type) {
        String name = ClassNameUtils.toInternalName(type.getClassName());
        //everything in dynamic runtime is object
        if (TypeUtils.isPackageAllowed(packages, name)) {
            return getSuperType(name);
        }

        return type;
    }

    public static Type checkedMethodType(List<String> packages, Type type) {
        Type[] argumentTypes = type.getArgumentTypes();
        Type returnType = checkedType(packages, type.getReturnType());

        int index = 0;
        for (Type at : argumentTypes) {
            String name = ClassNameUtils.toInternalName(at.getClassName());
            //everything in dynamic runtime is object
            if (TypeUtils.isPackageAllowed(packages, name)) {
                argumentTypes[index] = getSuperType(name);
            }

            index++;
        }

        return Type.getMethodType(returnType, argumentTypes);
    }

    private static Type getSuperType(String name) {
        SuperType superType = SuperTypesRegistry.instance().getSuperType(name);

        Objects.requireNonNull(superType, "SuperType not found for class: " + name);

        return Type.getObjectType(ClassNameUtils.toInternalName(superType.getType().getName()));
    }

    public static String checkedDesc(List<String> packages, String desc) {
        Type type = checkedMethodType(packages, Type.getMethodType(desc));

        return type.getDescriptor();
    }

    public static String checkedTypeDesc(List<String> packages, String desc) {
        Type descType = Type.getMethodType(desc).getReturnType();
        String checkedType = TypeUtils.checkedType(packages, descType).getDescriptor();

        return checkedType;
    }

    public static boolean isPathAvailable(List<String> allowedPackages, String filePath) {
        String unixPath = FilenameUtils.normalize(filePath);
        for (String pack : allowedPackages) {
            String packagePath = ClassNameUtils.toInternalName((pack));
            if (unixPath.startsWith(packagePath)) {
                return true;
            }
        }

        return false;
    }

    public static String checkedSignature(List<String> allowedPackages, String signature) {
        if (signature == null) {
            return null;
        }
        SignatureReader sr = new SignatureReader(signature);
        SignatureWriter sw = new DynamicSignatureWriter(allowedPackages);
        sr.accept(sw);
        return sw.toString();
    }

    public static boolean isDynamicSuperType(String classInternalName) {
        return classInternalName.endsWith(SuperType.SUFFIX);
    }

    public static Class<?> defineClass(String name, byte[] bytes) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        if (classLoader == null) {
            throw new RuntimeException("ClassLoader cannot be null");
        }

        if (bytes == null) {
            throw new RuntimeException("Bytes cannot be null");
        }

        name = ClassNameUtils.toJavaName(name);

        Class<?> definedClass;
        Class<?> loaderClass = classLoader.getClass();
        try {
            if (defineMethod == null) {
                defineMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                defineMethod.setAccessible(true);
            }
            definedClass = ((Class<?>) defineMethod.invoke(classLoader, name, bytes, 0, bytes.length));
            classLoader.loadClass(name);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("defineClass method not found on specified classLoader", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("defineClass invocation error", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("defineClass access error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Cannot load defined class: %s", name), e);
        }

        return definedClass;
    }

    public static void dumpClass(Class c, PrintStream out) {
        if (out == null) {
            out = System.out;
        }

        out.println(String.format("%s {", c.getName()));

        for (Field f : c.getDeclaredFields()) {
            out.println("\t" + f.toString());
        }

        out.println();

        for (Constructor constructor : c.getDeclaredConstructors()) {
            out.println("\t" + constructor.toString());
        }

        out.println();

        for (Method m : c.getDeclaredMethods()) {
            out.println("\t" + m.toString());
        }

        out.println("}");
    }

    public static void traceClassBytes(byte[] bytes, PrintWriter out) {
        if (out == null) {
            out = new PrintWriter(System.out);
        }

        ClassReader cr = new ClassReader(bytes);
        TraceClassVisitor cv = new TraceClassVisitor(out);
        cr.accept(cv, 0);


    }

    public static List<String> findDuplicatedMethods(byte[] bytes) {
        List<String> duplicatedMethods = new ArrayList<>();
        DynamicClassInfo info = new DynamicClassInfo(bytes);

        for (DynamicClassInfo.MethodInfo m : info.getMethods()) {
            boolean found = false;
            for (DynamicClassInfo.MethodInfo m2 : info.getMethods()) {
                if (m != m2 && m.getName().equals(m2.getName()) && m.getDesc().equals(m2.getDesc())) {
                    found = true;
                    break;
                }
            }

            if (found) {
                duplicatedMethods.add(m.getName() + m.getDesc() + ": ");
            }
        }

        return duplicatedMethods;
    }

    public static String generateDynamicMethodPrefix(String className) {
        return ClassNameUtils.toJavaName(className).replace(".", "_");
    }

    public static Class getReflectionCallingClass(String className, __PuccjObject instance) {
        className = ClassNameUtils.toJavaName(className);

        return getParentCallingClass(className, instance.getClass());
    }

    public static Class getParentCallingClass(String className, Class aClass) {
        if (aClass.getName().equals(className)) {
            return aClass;
        } else {
            if (aClass.getSuperclass() != null) {
                return getParentCallingClass(className, aClass.getSuperclass());
            }

            return null;
        }
    }

    public static boolean isPrimitive(String typeDescriptor) {
        return typeDescriptor.length() == 1;
    }

    public static Object getPrimitiveDefaultType(String typeDescriptor) {
        if (isPrimitive(typeDescriptor)) {
            Type type = Type.getType(typeDescriptor);
            if (Type.BOOLEAN_TYPE.equals(type)) {
                return false;
            }
            if (Type.CHAR_TYPE.equals(type)) {
                return 0;
            }
            if (Type.BYTE_TYPE.equals(type)) {
                return 0;
            }
            if (Type.SHORT_TYPE.equals(type)) {
                return 0;
            }
            if (Type.INT_TYPE.equals(type)) {
                return 0;
            }
            if (Type.FLOAT_TYPE.equals(type)) {
                return 0;
            }
            if (Type.LONG_TYPE.equals(type)) {
                return 0;
            }
            if (Type.DOUBLE_TYPE.equals(type)) {
                return 0;
            }
        }

        return null;
    }
}
