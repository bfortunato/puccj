package applica.puccj.resources.monitor;

/**
 * Created by bimbobruno on 02/12/15.
 */
public class Arguments {

    private String sourceDir;
    private String targetDir;


    public String getSourceDir() {
        return sourceDir;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void parse(String[] args) throws BadArgumentsException {
        if (args.length != 3) {
            throw new BadArgumentsException();
        }

        this.sourceDir = args[1];
        this.targetDir = args[2];
    }
}
