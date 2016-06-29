package applica.puccj.compiler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class SourceFile {

    private File file;

    public SourceFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public byte[] open() throws IOException {
        InputStream in = null;
        byte[] bytes = null;
        try {
            in = FileUtils.openInputStream(file);
            bytes = IOUtils.toByteArray(in);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return bytes;
    }

    @Override
    public String toString() {
        return file.toString();
    }
}
