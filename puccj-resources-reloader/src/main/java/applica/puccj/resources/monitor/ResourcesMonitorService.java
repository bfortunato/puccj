package applica.puccj.resources.monitor;

import applica.puccj.utils.PathUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.jci.monitor.FilesystemAlterationListener;
import org.apache.commons.jci.monitor.FilesystemAlterationMonitor;
import org.apache.commons.jci.monitor.FilesystemAlterationObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Created by bimbobruno on 02/12/15.
 */
public class ResourcesMonitorService implements FilesystemAlterationListener {

    private Log logger = LogFactory.getLog(getClass());

    private String sourceDir;
    private String targetDir;
    private boolean running;
    private FilesystemAlterationMonitor monitor;
    private Thread thread;

    public ResourcesMonitorService(String sourceDir, String targetDir) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
    }

    public void start() {
        Objects.requireNonNull(sourceDir);
        Objects.requireNonNull(targetDir);

        if (!new File(sourceDir).exists()) {
            throw new RuntimeException(String.format("%s not exists", sourceDir));
        }

        if (running) {
            throw new RuntimeException("Source watcher already running");
        }

        running = true;

        monitor = new FilesystemAlterationMonitor();
        monitor.addListener(new File(sourceDir), this);

        thread = new Thread(monitor);
        thread.setName("Single run source watcher");
        thread.start();

        logger.info(String.format("Scanning source at %s", sourceDir));
    }

    public void join() {
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        monitor.stop();
        monitor = null;

        logger.info(String.format("Stopped resources scanning at %s", sourceDir));
    }


    @Override
    public void onStart(FilesystemAlterationObserver filesystemAlterationObserver) {

    }

    @Override
    public void onFileCreate(File file) {
        String relativeSourcePath = PathUtils.relative(sourceDir, file.getAbsolutePath());
        String relativeSourceDir = PathUtils.relative(sourceDir, FilenameUtils.getFullPath(file.getAbsolutePath()));
        String absoluteTargetPath = FilenameUtils.concat(targetDir, relativeSourcePath);
        String absoluteTargetDir = FilenameUtils.concat(targetDir, relativeSourceDir);
        try {
            FileUtils.forceMkdir(new File(absoluteTargetDir));
        } catch (IOException e) {
            System.err.print("Cannot create directory: " + absoluteTargetDir);
        }

        try {
            FileUtils.copyFile(file, new File(absoluteTargetPath));
        } catch (IOException e) {
            System.err.print(String.format("Cannot create file %s: %s", absoluteTargetPath, e.getMessage()));
        }

        System.out.println(String.format("%s packaged", absoluteTargetPath));
    }

    @Override
    public void onFileChange(File file) {
        onFileCreate(file);
    }

    @Override
    public void onFileDelete(File file) {
        String relativeSourcePath = PathUtils.relative(sourceDir, file.getAbsolutePath());
        String absoluteTargetPath = FilenameUtils.concat(targetDir, relativeSourcePath);

        try {
            FileUtils.forceDelete(new File(absoluteTargetPath));
        } catch (IOException e) {
            System.err.print(String.format("Cannot create file %s: %s", absoluteTargetPath, e.getMessage()));
        }

        System.out.println(String.format("%s deleted", absoluteTargetPath));
    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }

    @Override
    public void onStop(FilesystemAlterationObserver filesystemAlterationObserver) {

    }
}
