package applica.puccj.compiler;

import applica.puccj.projects.Project;
import applica.puccj.utils.PathUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.jci.compilers.CompilationResult;
import org.apache.commons.jci.compilers.JavaCompiler;
import org.apache.commons.jci.compilers.JavaCompilerFactory;
import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.apache.commons.jci.problems.CompilationProblem;
import org.apache.commons.jci.readers.FileResourceReader;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.FileResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class Compiler implements SourceWatcher.SourceChangeListener {

    public interface CompilationListener {
        void onCompilationComplete(List<CompiledFile> compiledFiles);
    }

    private Log logger = LogFactory.getLog(getClass());
    private List<CompilationListener> listeners = new ArrayList<>();
    private File sourcesDir;
    private File classesDir;

    public Compiler(Project project) {
        this.sourcesDir = new File(project.getSourcesDir());
        this.classesDir = new File(project.getTargetDir());
    }

    public void addListener(CompilationListener listener) {
        listeners.add(listener);
    }

    public List<CompiledFile> compile(List<SourceFile> sources) {
        String compilerClass = EclipseJava18Compiler.class.getName();
        JavaCompiler compiler = new JavaCompilerFactory().createCompiler(compilerClass);
        JavaCompilerSettings settings = new EclipseJava18CompilerSettings(compiler.createDefaultSettings());
        String javaVersion = System.getProperty("java.version").substring(0, 3);
        settings.setSourceVersion(javaVersion);
        settings.setTargetVersion(javaVersion);
        Objects.requireNonNull(compiler);

        ResourceReader resourceReader = new FileResourceReader(sourcesDir);
        ResourceStore resourceStore = new FileResourceStore(classesDir);
        String[] sourcePaths = new String[sources.size()];

        int i = 0;
        for (SourceFile c : sources) {
            sourcePaths[i++] = relativePath(sourcesDir, c.getFile().getPath());
        }

        for (String file : sourcePaths) {
            logger.debug(String.format("Compiling %s", file));
        }

        CompilationResult result = compiler.compile(sourcePaths, resourceReader, resourceStore, Thread.currentThread().getContextClassLoader(), settings);

        for (CompilationProblem problem : result.getWarnings()) {
            logger.warn(problem.toString());
            System.err.println(problem.toString());
        }

        for (CompilationProblem problem : result.getErrors()) {
            logger.error(problem.toString());
            System.err.println(problem.toString());
        }

        List<CompiledFile> compiledFiles = new ArrayList<>();
        for (String file : sourcePaths) {
            boolean error = false;
            for (CompilationProblem problem : result.getErrors()) {
                if (problem.getFileName().equals(file)) {
                    error = true;
                    break;
                }
            }

            if (!error) {
                String classInternalName = file.replace("\\", "/").substring(0, file.length() - ".java".length());
                String classFilePath = FilenameUtils.concat(classesDir.getPath(), classInternalName + ".class");
                compiledFiles.add(new CompiledFile(classInternalName, new File(classFilePath)));

                addInnerClasses(compiledFiles, classFilePath);
            }
        }

        if (compiledFiles.size() > 0) {
            for (CompilationListener listener : listeners) {
                listener.onCompilationComplete(compiledFiles);
            }
        }

        return compiledFiles;
    }

    private void addInnerClasses(List<CompiledFile> compiledFiles, String classFilePath) {
        String dir = FilenameUtils.getFullPath(classFilePath);
        final String innerClassPrefix = FilenameUtils.getBaseName(classFilePath) + "$";
        IOFileFilter filter = new IOFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().startsWith(innerClassPrefix);
            }

            @Override
            public boolean accept(File dir, String name) {
                return false;
            }
        };

        Collection<File> innerClasses = FileUtils.listFiles(new File(dir), filter, null);

        for (File innerClass : innerClasses) {
            String path = PathUtils.relative(classesDir.getAbsolutePath(), innerClass.getAbsolutePath());
            String classInternalName = path.replace("\\", "/").substring(0, path.length() - ".class".length());

            logger.info(String.format("Inner class found: %s", classInternalName));
            compiledFiles.add(new CompiledFile(classInternalName, innerClass));
        }
    }

    private String relativePath(File sourcesDir, String path) {
        String source = path.replace(sourcesDir.getAbsolutePath(), "");
        if (source.startsWith("/") || source.startsWith("\\")) {
            source = source.substring(1);
        }

        return source;
    }

    @Override
    public void onSourceChanged(List<SourceFile> createdFiles, List<SourceFile> changedFiles) {
        List<SourceFile> join = new ArrayList<>(createdFiles);
        join.addAll(changedFiles);

        if (join.size() > 0) {
            compile(join);
        }
    }
}
