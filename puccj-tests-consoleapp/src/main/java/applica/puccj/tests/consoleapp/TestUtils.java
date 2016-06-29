package applica.puccj.tests.consoleapp;

import applica.puccj.compiler.SourceFile;
import org.apache.commons.io.FileUtils;

import java.util.List;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class TestUtils {

    public static void modifyAndWait(List<SourceFile> sourceFiles, boolean skipStart) {
        for (SourceFile file : sourceFiles) {
            try {
                if (!skipStart) {
                    System.out.println("Touching file " + file.getFile().getAbsolutePath());
                    FileUtils.touch(file.getFile());
                }

                skipStart = !skipStart;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForChanges() {
        try {
            System.out.println("Do your changes NOW");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

