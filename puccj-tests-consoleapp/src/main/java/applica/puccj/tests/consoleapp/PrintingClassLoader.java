package applica.puccj.tests.consoleapp;

/**
 * Created by bimbobruno on 23/01/16.
 */
public class PrintingClassLoader extends ClassLoader {

    public PrintingClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println(name);

        return getParent().loadClass(name);
    }
}
