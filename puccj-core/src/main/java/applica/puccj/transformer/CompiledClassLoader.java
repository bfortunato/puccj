package applica.puccj.transformer;

import applica.puccj.compiler.CompiledFile;
import applica.puccj.runtime.classes.ClassDefinition;
import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class CompiledClassLoader extends ClassLoader {

    private Log logger = LogFactory.getLog(getClass());

    private List<String> packages;

    private HashMap<String, Class<?>> transformationCache = new HashMap<>();
    private List<CompiledFile> currentTransformationRequest = new ArrayList<>();

    public CompiledClassLoader(List<String> allowedPackages, ClassLoader parent) {
        super(parent);

        this.packages = allowedPackages;
    }

    private void loadCompiledFile(CompiledFile compiledFile) {
        String className = ClassNameUtils.toJavaName(compiledFile.getClassInternalName());

        if (transformationCache.containsKey(className)) {
            return;
        }

        String classInternalName = ClassNameUtils.toInternalName(compiledFile.getClassInternalName());

        Class c = null;
        try {
            c = super.defineClass(className, compiledFile.open(), 0, compiledFile.open().length, null);

            System.out.println(String.format("%s reloaded", className));

        } catch (IOException e) {
            logger.warn(String.format("Cannot define class: %s", className));
        }

        if (c != null) {
            transformationCache.put(className, c);

            ClassesRegistry.instance().registerClass(classInternalName, c, this);

            logger.info(String.format("Compiled class added to registry: %s", className));
        }
    }

    public void loadCompiledFiles(List<CompiledFile> compiledFiles) {
        currentTransformationRequest.clear();
        currentTransformationRequest.addAll(compiledFiles);

        for (CompiledFile f : compiledFiles) {
            loadCompiledFile(f);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        String classInternalName = ClassNameUtils.toInternalName(name);

        if (TypeUtils.isPackageAllowed(packages, classInternalName)) {
            if (TypeUtils.isDynamicSuperType(classInternalName)) {
                SuperType superType = SuperTypesRegistry.instance().getSuperType(classInternalName);
                if (superType != null) {
                    return superType.getType();
                }
            }

            if (isInCurrentTransformation(name)) {
                loadCompiledFile(getCurrentTransformationByClassName(name));
                return transformationCache.get(name);
            }

            ClassDefinition definition = ClassesRegistry.instance().getClassDefinition(classInternalName);
            if (definition != null) {
                logger.info(String.format("Loading compiled class from registry: %s", name));

                return definition.getDefinedClass();
            }

            //logger.warn(String.format("%s is allowed but not compiled from puccj. Maybe is a CGLib enhanced class?", classInternalName));
        }

        return super.loadClass(name);
    }

    private CompiledFile getCurrentTransformationByClassName(String name) {
        String internalName = ClassNameUtils.toInternalName(name);

        for (CompiledFile f : currentTransformationRequest) {
            if (f.getClassInternalName().equals(internalName)) {
                return f;
            }
        }

        throw new RuntimeException("Current transformation not found");
    }

    private boolean isInCurrentTransformation(String name) {
        String internalName = ClassNameUtils.toInternalName(name);

        for (CompiledFile f : currentTransformationRequest) {
            if (f.getClassInternalName().equals(internalName)) {
                return true;
            }
        }

        return false;
    }

    public Class defineClass(String className, byte[] bytes) {
        return super.defineClass(className, bytes, 0, bytes.length, null);
    }
}
