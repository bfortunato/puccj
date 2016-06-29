package applica.puccj.runtime.classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class ClassDefinition {

    private String internalName;
    private Class<?> definedClass;
    private int version;
    private ClassLoader classLoader;
    private List<Class> oldClasses = new ArrayList<>();

    public ClassDefinition() {}

    public ClassDefinition(String internalName, Class<?> definedClass, ClassLoader classLoader) {
        this.internalName = internalName;
        this.definedClass = definedClass;
        this.version = 1;
        this.classLoader = classLoader;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Class<?> getDefinedClass() {
        return definedClass;
    }

    public void setDefinedClass(Class<?> newDefinedClass) {
        if (this.definedClass != null) {
            oldClasses.add(this.definedClass);
        }

        this.definedClass = newDefinedClass;
    }

    public int getVersion() {
        return version;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void incVersion() {
        version++;
    }

    public List<Class> getOldClasses() {
        return oldClasses;
    }

}
