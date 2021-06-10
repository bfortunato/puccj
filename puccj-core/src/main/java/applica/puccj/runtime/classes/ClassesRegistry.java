package applica.puccj.runtime.classes;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.__PuccjObject;
import applica.puccj.utils.ClassNameUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class ClassesRegistry {

    private Log logger = LogFactory.getLog(getClass());

    private static ClassesRegistry s_instance;

    private ClassesRegistry() {

    }

    public static ClassesRegistry instance() {
        if (s_instance == null) {
            s_instance = new ClassesRegistry();
        }

        return s_instance;
    }

    private List<ClassDefinition> classDefinitions = Collections.synchronizedList(new ArrayList<ClassDefinition>());

    public void registerClass(String internalName, Class<?> newClass, ClassLoader classLoader) {
        ClassDefinition definition = getClassDefinition(internalName);

        if (definition == null) {
            definition = new ClassDefinition();
            definition.setInternalName(internalName);
            definition.setDefinedClass(newClass);
            definition.setClassLoader(classLoader);

            classDefinitions.add(definition);

            logger.info(String.format("Class definition created: %s", internalName));
        } else if (!newClass.equals(definition.getDefinedClass())) {
            definition.setDefinedClass(newClass);
            definition.setClassLoader(classLoader);
            definition.incVersion();

            logger.info(String.format("New %s version: %d", internalName, definition.getVersion()));
        }
    }

    public Class loadClass(String className) throws ClassNotFoundException {
        ClassDefinition classDefinition = getClassDefinition(ClassNameUtils.toInternalName(className));
        if (classDefinition != null && classDefinition.getDefinedClass() != null) {
            return classDefinition.getDefinedClass();
        } else {
            return Thread.currentThread().getContextClassLoader().loadClass(ClassNameUtils.toJavaName(className));
        }
    }

    public Class loadClass(String className, __PuccjObject instance) throws ClassNotFoundException {
        className = ClassNameUtils.toJavaName(className);
        ClassDefinition classDefinition = getClassDefinition(ClassNameUtils.toInternalName(className));
        if (classDefinition != null && classDefinition.getDefinedClass() != null) {
            return classDefinition.getDefinedClass();
        } else {
            if (instance.getClass().getName().equals(className)) {
                return instance.getClass();
            } else {
                return Thread.currentThread().getContextClassLoader().loadClass(className);
            }
        }
    }

    /*private ClassDefinition getOrCreateClassDefinition(final String internalName) {
        ClassDefinition definition = ((ClassDefinition) CollectionUtils.find(classDefinitions, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                ClassDefinition c = ((ClassDefinition) o);
                return c.getInternalName().equals(internalName);
            }
        }));

        if (definition == null) {
            definition = new ClassDefinition();
            definition.setInternalName(internalName);
            classDefinitions.add(definition);

            logger.info(String.format("Class definition created: %s", internalName));
        }

        return definition;
    }
    */

    public ClassDefinition getClassDefinition(final String internalName) {
        ClassDefinition definition = ((ClassDefinition) CollectionUtils.find(classDefinitions, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                ClassDefinition c = ((ClassDefinition) o);
                return c.getInternalName().equals(internalName);
            }
        }));

        return definition;
    }

    public Class getLatestVersionOfClass(Class clazz) {
        DynamicRuntime.instance().checkPackage(clazz.getName());

        String internalName = ClassNameUtils.toInternalName(clazz.getName());
        ClassDefinition definition = getClassDefinition(internalName);
        if (definition != null && definition.getDefinedClass() != null) {
            return definition.getDefinedClass();
        }

        return clazz;
    }

    public boolean isLatestVersionOfClass(Class clazz) {
        DynamicRuntime.instance().checkPackage(clazz.getName());

        String internalName = ClassNameUtils.toInternalName(clazz.getName());
        ClassDefinition classDefinition = getClassDefinition(internalName);

        if (classDefinition == null) {
            throw new RuntimeException(String.format("Class definition not found: %s"));
        }

        return classDefinition.getDefinedClass().equals(clazz);
    }








    /**
     * Internal method, do not use
     * @param className
     * @return
     */
    public Class __loadClass(String className) {
        ClassDefinition classDefinition = getClassDefinition(ClassNameUtils.toInternalName(className));
        if (classDefinition != null && classDefinition.getDefinedClass() != null) {
            return classDefinition.getDefinedClass();
        } else {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(ClassNameUtils.toJavaName(className));
            } catch (Exception e) {
                throw new RuntimeException("__loadClass");
            }
        }
    }

    /**
     * Internal method, do not use
     * @param className
     * @return
     */
    public Class __loadOriginalClass(String className) {
        ClassDefinition classDefinition = null;//getClassDefinition(ClassNameUtils.toInternalName(className));
        if (classDefinition != null && classDefinition.getDefinedClass() != null) {
            return classDefinition.getDefinedClass();
        } else {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(ClassNameUtils.toJavaName(className));
            } catch (Exception e) {
                throw new RuntimeException("__loadOriginalClass");
            }
        }
    }
}
