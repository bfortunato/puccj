package applica.puccj.utils;

import java.io.File;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class FileWalker {

    public interface FileWalkerListener {
        public void onFile(File root, File absolutePath);
    }

    public void walk(String path, FileWalkerListener fileWalkerListener) {

        File root = new File(path);
        File[] list = root.listFiles();

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), fileWalkerListener);
            } else {
                fileWalkerListener.onFile(root, f.getAbsoluteFile());
            }
        }
    }

}