package applica.puccj.projects;

import java.util.List;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class Project {

    private String sourcesDir;
    private String targetDir;
    private List<String> packages;
    private String name;

    public Project(String sourcesDir, String targetDir, List<String> packages) {
        this.sourcesDir = sourcesDir;
        this.targetDir = targetDir;
        this.packages = packages;
    }

    public Project() {
    }

    public String getSourcesDir() {
        return sourcesDir;
    }

    public void setSourcesDir(String sourcesDir) {
        this.sourcesDir = sourcesDir;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
