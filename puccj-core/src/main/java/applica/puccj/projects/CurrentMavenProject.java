package applica.puccj.projects;

import org.apache.commons.io.FilenameUtils;

import java.util.List;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class CurrentMavenProject extends Project {

    public CurrentMavenProject(List<String> packages) {
        this.setPackages(packages);

        String classesDir = getClass().getResource("/").getPath();
        String sourcesDir = FilenameUtils.separatorsToUnix(classesDir);
        int i = FilenameUtils.indexOfLastSeparator(sourcesDir);
        sourcesDir = sourcesDir.substring(0, i);
        i = FilenameUtils.indexOfLastSeparator(sourcesDir);
        sourcesDir = sourcesDir.substring(0, i);
        i = FilenameUtils.indexOfLastSeparator(sourcesDir);
        sourcesDir = sourcesDir.substring(0, i);
        sourcesDir = FilenameUtils.concat(sourcesDir, "src/main/java");

        setTargetDir(classesDir);
        setSourcesDir(sourcesDir);
    }

}
