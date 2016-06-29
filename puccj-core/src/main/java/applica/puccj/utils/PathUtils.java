package applica.puccj.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by bimbobruno on 21/10/15.
 */
public class PathUtils {

    public static String relative(String root, String absolute) {
        String path = absolute.replace(root, "");
        if (path.startsWith("//") || path.startsWith("/")) {
            return path.substring(1);
        }

        return path;
    }

    public static String getClassFilePath(Class cl) {
        String classFile = ClassNameUtils.toInternalName(cl.getName()).concat(".class");
        String path = cl.getClassLoader().getResource(classFile).getPath();
        return path;
    }

    public static String createTempTargetDirectory() throws IOException {
        String property = "java.io.tmpdir";
        String tempDir = System.getProperty(property);
        UUID uuid = UUID.randomUUID();
        tempDir = FilenameUtils.concat(tempDir, uuid.toString());

        File f = new File(tempDir);
        FileUtils.forceMkdir(f);

        return f.getAbsolutePath();
    }


}
