package applica.puccj.resources.monitor;

/**
 * Created by bimbobruno on 02/12/15.
 */
public class ResourcesMonitor {

    private static String usage() {
        StringBuilder builder = new StringBuilder();
        builder.append("usage: java -jar puccj-resource-monitor.jar [sourceDir] [targetDir]\n");
        builder.append("\t\t(to monitor resource changes in a dir)\n");
        builder.append("\n");

        return builder.toString();
    }

    public static void main(String[] args) {
        Arguments arguments = new Arguments();
        try {
            arguments.parse(args);
        } catch (BadArgumentsException e) {
            System.out.println(usage());
            return;
        }

        ResourcesMonitorService service = new ResourcesMonitorService(arguments.getSourceDir(), arguments.getTargetDir());
        service.start();

        System.out.println(String.format("Resource monitor running on %s. Press Ctrl+C to stop monitoring", arguments.getSourceDir()));

        service.join();
    }

}
