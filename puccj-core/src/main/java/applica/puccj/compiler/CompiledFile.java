package applica.puccj.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class CompiledFile {

    private String classInternalName;
    private File file;
    private byte[] bytes;

    public CompiledFile(String classInternalName, File file) {
        this.classInternalName = classInternalName;
        this.file = file;
    }

    public String getClassInternalName() {
        return classInternalName;
    }

    public void setClassInternalName(String classInternalName) {
        this.classInternalName = classInternalName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] open() throws IOException {
        if (bytes == null) {
            InputStream in = null;
            try {
                in = FileUtils.openInputStream(file);
                bytes = IOUtils.toByteArray(in);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }

        return bytes;
    }

    @Override
    public String toString() {
        return classInternalName;
    }
}
