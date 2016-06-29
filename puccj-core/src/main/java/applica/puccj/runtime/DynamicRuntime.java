package applica.puccj.runtime;

import applica.puccj.compiler.CompiledFile;
import applica.puccj.compiler.Compiler;
import applica.puccj.compiler.SourceFile;
import applica.puccj.compiler.SourceWatcher;
import applica.puccj.projects.Project;
import applica.puccj.runtime.classes.ClassesRegistry;
import applica.puccj.runtime.classes.RefreshConstructorParameter;
import applica.puccj.runtime.code.CodeSpace;
import applica.puccj.runtime.memory.MemorySpace;
import applica.puccj.transformer.CompiledClassLoader;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.FileWalker;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 10/10/15.
 */
public class DynamicRuntime {

    final private static DynamicRuntime s_instance = new DynamicRuntime();
    private List<String> allowedPackages = new ArrayList<>();
    private List<SourceFile> sources;
    private boolean running;
    private List<SourceWatcher> sourceWatchers = new ArrayList<>();

    private long instances = 0;

    private DynamicRuntime() {
        memory = new MemorySpace();
        code = new CodeSpace();
    }

    public static DynamicRuntime instance() {
        return s_instance;
    }

    private Log logger = LogFactory.getLog(getClass());

    private MemorySpace memory;
    private CodeSpace code;

    public MemorySpace getMemory() {
        return memory;
    }

    public CodeSpace getCode() {
        return code;
    }

    public void start(final List<Project> projects) {
        running = true;

        List<String> packages = new ArrayList<>();

        for (Project p : projects) {
            packages.addAll(p.getPackages());
        }

        this.allowedPackages = packages;

        collectSources(projects);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (final Project project : projects) {
                    SourceWatcher sourceWatcher = new SourceWatcher(project);
                    Class compilerClass = null;
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    try {
                        compilerClass = cl.loadClass("applica.puccj.compiler.Compiler");
                    } catch (ClassNotFoundException e) {

                    }

                    Compiler compiler = null;

                    if (compilerClass != null) {
                        try {
                            compiler = (Compiler) compilerClass.getConstructor(Project.class).newInstance(project);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (compiler == null) {
                        compiler = new Compiler(project);
                    }

                    sourceWatcher.addListener(compiler);
                    compiler.addListener(new Compiler.CompilationListener() {
                        @Override
                        public void onCompilationComplete(List<CompiledFile> compiledFiles) {
                            CompiledClassLoader classLoader = new CompiledClassLoader(allowedPackages, getClass().getClassLoader());
                            classLoader.loadCompiledFiles(compiledFiles);
                        }
                    });

                    sourceWatcher.start();
                    sourceWatchers.add(sourceWatcher);
                }
            }
        }).start();
    }

    private void collectSources(List<Project> projects) {
        sources = new ArrayList<>();
        FileWalker walker = new FileWalker();
        for (Project project : projects) {
            walker.walk(project.getSourcesDir(), new FileWalker.FileWalkerListener() {
                @Override
                public void onFile(File root, File absolutePath) {
                    if (FilenameUtils.getExtension(absolutePath.getAbsolutePath()).endsWith("java")) {
                        sources.add(new SourceFile(absolutePath));
                    }
                }
            });
        }
    }

    /*
    public void makeProjectsDynamic(List<Project> projects) {
        final List<String> packages = new ArrayList<>();

        CompiledClassLoader classLoader = new CompiledClassLoader(packages, Thread.currentThread().getContextClassLoader());
        for (Project project : projects) {
            Compiler compiler = new Compiler(project);
            classLoader.loadCompiledFiles(compiler.compile(sources));
        }
    }
    */

    public void stop() {
        running = false;

        for (SourceWatcher sourceWatcher : sourceWatchers) {
            sourceWatcher.stop();
        }
    }

    public boolean isPackageAllowed(String className) {
        className = ClassNameUtils.toJavaName(className);

        return TypeUtils.isPackageAllowed(allowedPackages, className);
    }

    public void checkPackage(String className) {
        if (!isPackageAllowed(className)) {
            throw new RuntimeException(String.format("Package now allowed: %s", className));
        }
    }

    public List<String> getAllowedPackages() {
        return allowedPackages;
    }

    public List<SourceFile> getSources() {
        return sources;
    }

    public boolean isRunning() {
        return running;
    }

    public long nextInstanceId() {
        return ++instances;
    }

    public __PuccjObject invokableInstance(RuntimeType owner, __PuccjObject instance) {
        Class initiator = null;
        String instanceInternalName = ClassNameUtils.toInternalName(instance.getClass().getName());
        String ownerInternalName = owner.getClassInternalName();
        String ownerName = ClassNameUtils.toJavaName(owner.getClassInternalName());
        if (!instanceInternalName.equals(ownerInternalName)) {
            Class superOwner = TypeUtils.getParentCallingClass(ownerName, instance.getClass());

            if (superOwner != null && !superOwner.isInterface()) {
                Class latestOwnerClass = null;
                try {
                    latestOwnerClass = ClassesRegistry.instance().loadClass(ClassNameUtils.toJavaName(ownerInternalName));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (latestOwnerClass != null) {
                    if (superOwner != latestOwnerClass) {
                        initiator = latestOwnerClass;
                    }
                }
            }
        }

        if (initiator == null) {
            initiator = ClassesRegistry.instance().getLatestVersionOfClass(instance.getClass());
        }

        if (instance.getClass().equals(initiator)) {
            return instance;
        } else {
            try {
                __PuccjObject newInstance = (__PuccjObject) initiator.getConstructor(RefreshConstructorParameter.class).newInstance(new RefreshConstructorParameter());
                newInstance.__puccj_setInstanceId(instance.__puccj_getInstanceId());
                return newInstance;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
