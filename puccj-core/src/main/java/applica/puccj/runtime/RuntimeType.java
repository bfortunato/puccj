package applica.puccj.runtime;

import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.runtime.classes.RefreshConstructorParameter;
import applica.puccj.utils.ClassNameUtils;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class RuntimeType {

    private String classInternalName;

    public RuntimeType(String classInternalName) {
        this.classInternalName = ClassNameUtils.toInternalName(classInternalName);
    }

    public String getClassInternalName() {
        return classInternalName;
    }

    public void setClassInternalName(String classInternalName) {
        this.classInternalName = ClassNameUtils.toInternalName(classInternalName);;
    }

    @Override
    public boolean equals(Object obj) {
        RuntimeType other = ((RuntimeType) obj);
        if (other == null) {
            return false;
        }

        return classInternalName.equals(other.classInternalName);
    }

    @Override
    public String toString() {
        return String.format("%s", classInternalName);
    }

    public Object newInstance() {
        try {
            //this method use an empty constructor to refresh the instance. This constructor accept ClassesRegistry
            Class clazz = ClassesRegistry.instance().loadClass(classInternalName);
            return clazz.getConstructor(RefreshConstructorParameter.class).newInstance(new RefreshConstructorParameter());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAssignableFrom(RuntimeType obj) {
        RuntimeType other = ((RuntimeType) obj);
        if (other == null) {
            return false;
        }

        return classInternalName.startsWith(other.classInternalName);
    }
}
