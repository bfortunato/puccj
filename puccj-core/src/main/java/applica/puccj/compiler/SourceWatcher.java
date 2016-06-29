package applica.puccj.compiler;

import applica.puccj.projects.Project;
import applica.puccj.utils.PathUtils;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.jci.monitor.FilesystemAlterationListener;
import org.apache.commons.jci.monitor.FilesystemAlterationMonitor;
import org.apache.commons.jci.monitor.FilesystemAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class SourceWatcher implements FilesystemAlterationListener {

    public interface SourceChangeListener {
        void onSourceChanged(List<SourceFile> createdFiles, List<SourceFile> changedFiles);
    }

    public static final String JAVAEXT = "java";

    private Log logger = LogFactory.getLog(getClass());

    public SourceWatcher(Project project) {
        this.project = project;
    }

    public SourceWatcher(Project project, boolean forceFirstCheck) {
        this.project = project;
        this.firstTime = !forceFirstCheck;
    }

    private boolean running = false;
    private FilesystemAlterationMonitor monitor;
    private boolean firstTime = true;
    private Project project;
    private List<SourceFile> createdSourceFiles = new ArrayList<>();
    private List<SourceFile> changedSourceFiles = new ArrayList<>();
    private List<SourceChangeListener> listeners = new ArrayList<>();

    public boolean isRunning() {
        return running;
    }

    public List<SourceFile> getCreatedSourceFiles() {
        return createdSourceFiles;
    }

    public List<SourceFile> getChangedSourceFiles() {
        return changedSourceFiles;
    }

    public void runSyncOneTime() {
        Objects.requireNonNull(project);

        if (running) {
            throw new RuntimeException("Source watcher already running");
        }

        running = true;

        monitor = new FilesystemAlterationMonitor();
        monitor.addListener(new File(project.getSourcesDir()), this);

        logger.debug(String.format("Scanning source at %s", project.getSourcesDir()));

        Thread thread = new Thread(monitor);
        thread.setName("Single run source watcher");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        stop();
    }

    public void start() {
        Objects.requireNonNull(project);

        if (running) {
            throw new RuntimeException("Source watcher already running");
        }

        running = true;

        monitor = new FilesystemAlterationMonitor();
        monitor.addListener(new File(project.getSourcesDir()), this);
        monitor.start();

        logger.debug(String.format("Scanning source at %s", project.getSourcesDir()));
    }

    public void stop() {
        running = false;
        monitor.stop();
        monitor = null;


        logger.debug(String.format("Stopped source scanning at %s", project.getSourcesDir()));
    }

    public void clear() {
        createdSourceFiles.clear();
        changedSourceFiles.clear();
    }

    public void addListener(SourceChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onStart(FilesystemAlterationObserver pObserver) {
    }

    @Override
    public void onFileCreate(File pFile) {
        if (FilenameUtils.getExtension(pFile.getName()).equals(JAVAEXT)) {
            if (isPathAvailable(PathUtils.relative(project.getSourcesDir(), pFile.getAbsolutePath()))) {
                logger.debug(String.format("New source file created: %s", pFile.getPath()));
                createdSourceFiles.add(new SourceFile(pFile));
            }
        }
    }

    @Override
    public void onFileChange(File pFile) {
        if (FilenameUtils.getExtension(pFile.getName()).equals(JAVAEXT)) {
            if (isPathAvailable(PathUtils.relative(project.getSourcesDir(), pFile.getAbsolutePath()))) {
                logger.debug(String.format("Source file changed: %s", pFile.getPath()));
                changedSourceFiles.add(new SourceFile(pFile));
            }
        }
    }

    @Override
    public void onFileDelete(File pFile) {

    }

    @Override
    public void onDirectoryCreate(File pDir) {

    }

    @Override
    public void onDirectoryChange(File pDir) {

    }

    @Override
    public void onDirectoryDelete(File pDir) {

    }

    @Override
    public void onStop(FilesystemAlterationObserver pObserver) {
        if (!firstTime) {
            int size = createdSourceFiles.size() + changedSourceFiles.size();
            if (size > 0) {
                for (SourceChangeListener listener : listeners) {
                    try {
                        listener.onSourceChanged(createdSourceFiles, changedSourceFiles);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
        createdSourceFiles.clear();
        changedSourceFiles.clear();
        firstTime = false;
    }

    protected boolean isPathAvailable(String filePath) {
        return TypeUtils.isPathAvailable(project.getPackages(), filePath);
    }
}
