package applica.puccj.utils;

/**
 * Created by bimbobruno on 15/10/15.
 */
public class ClassNameUtils {

    public static String toInternalName(String className) {
        return className.replace(".", "/");
    }

    public static String toJavaName(String className) {
        return className.replace("/", ".");
    }

}
