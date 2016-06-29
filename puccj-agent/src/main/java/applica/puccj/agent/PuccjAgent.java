package applica.puccj.agent;

import applica.puccj.projects.Project;
import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.classes.RefreshConstructorParameter;
import applica.puccj.transformer.DynamicClassInfo;
import applica.puccj.transformer.RefreshConstructorClassVisitor;
import applica.puccj.transformer.SuperType;
import applica.puccj.transformer.Transformer;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.PathUtils;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by bimbobruno on 13/01/16.
 */
public class PuccjAgent implements ClassFileTransformer {

    public static final String CONFIG_PROJECTS = "puccj.projects";
    public static final String CONFIG_PROJECT_PACKAGES = "puccj.%s.packages";
    public static final String CONFIG_PROJECT_SOURCESDIR = "puccj.%s.sourcesDir";
    public static final String CONFIG_PROJECT_TARGETDIR = "puccj.%s.targetDir";
    public static final String CONFIG_LOG_ENABLED = "puccj.log.enabled";

    private Log logger = LogFactory.getLog(getClass());

    private Instrumentation instrumentation;

    public PuccjAgent(Instrumentation inst) {
        this.instrumentation = inst;
    }

    public static void premain(String args, Instrumentation inst) {
        Transformer.traceEnabled = false;
        Transformer.checkEnabled = false;
        //Transformer.tracePath = "/Users/bimbobruno/Desktop/bytecode/";

        PuccjAgent agent = new PuccjAgent(inst);
        agent.run(args);
    }

    private void run(String args) {
        try {
            List<Project> projects = loadProjectsFromConfiguration(args);

            if (projects.size() == 0) {
                logger.warn("Error loading projects");

                return;
            }

            DynamicRuntime.instance().start(projects);

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    DynamicRuntime.instance().stop();
                }
            }));

            instrumentation.addTransformer(this);

            //DynamicRuntime.instance().makeProjectsDynamic(projects);
        } catch (ProjectLoadException e) {
            logger.warn("Error loading projects");

            return;
        }


    }

    private List<Project> loadProjectsFromConfiguration(String args) throws ProjectLoadException {
        List<Project> projects = new ArrayList<>();

        System.out.println(String.format("puccj working directory: %s", new File(".").getAbsolutePath()));

        InputStream configurationIn = null;
        if (StringUtils.isNotEmpty(args)) {
            try {
                configurationIn = FileUtils.openInputStream(new File(args));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //load configuration
            configurationIn = getClass().getResourceAsStream("/puccj.properties");
            if (configurationIn == null) {
                getClass().getClassLoader().getResourceAsStream("puccj.properties");
            }
        }


        if (configurationIn == null) {
            logger.warn("puccj.properties not found");

            throw new ProjectLoadException();
        }

        Properties properties = new Properties();
        try {
            properties.load(configurationIn);
        } catch (IOException e) {
            logger.warn("could not load puccj.properties");

            throw new ProjectLoadException();
        }

        IOUtils.closeQuietly(configurationIn);

        List<String> projectNames = getCommaSeparatedValues(properties.getProperty(CONFIG_PROJECTS));
        if (projectNames.size() == 0) {
            logger.warn(String.format("No projects found on puccj.properties (property: %s)", CONFIG_PROJECTS));

            throw new ProjectLoadException();
        }

        for (String projectName : projectNames) {
            Project project = new Project();
            project.setName(projectName);

            String packagesValue = properties.getProperty(String.format(CONFIG_PROJECT_PACKAGES, projectName));
            project.setPackages(getCommaSeparatedValues(packagesValue));

            if (project.getPackages().size() == 0) {
                logger.warn(String.format("No packages found on puccj.properties (project: %s, property: %s)", projectName, CONFIG_PROJECT_PACKAGES));

                throw new ProjectLoadException();
            }


            String sourcesDir = properties.getProperty(String.format(CONFIG_PROJECT_SOURCESDIR, projectName));
            if (StringUtils.isEmpty(sourcesDir)) {
                logger.warn(String.format("Sources dir not specified in puccj.properties (project: %s, property: %s)", projectName, CONFIG_PROJECT_SOURCESDIR));

                throw new ProjectLoadException();
            }

            if (!new File(sourcesDir).exists()) {
                logger.warn(String.format("Sources dir not found (project: %s, value: %s)", projectName, sourcesDir));

                throw new ProjectLoadException();
            }

            String targetDir = properties.getProperty(String.format(CONFIG_PROJECT_TARGETDIR, projectName));
            if (StringUtils.isEmpty(targetDir)) {
                try {
                    targetDir = PathUtils.createTempTargetDirectory();
                } catch (IOException e) {
                    logger.warn(String.format("Could not create target dir (project: %s)", projectName));

                    throw new ProjectLoadException();
                }
            } else {
                if (!new File(targetDir).exists()) {
                    logger.warn(String.format("Target dir not found (project: %s, value: %s)", projectName, targetDir));

                    throw new ProjectLoadException();
                }
            }

            project.setSourcesDir(sourcesDir);
            project.setTargetDir(targetDir);

            projects.add(project);

            logger.trace(String.format("Project %s configured {", projectName));
            logger.trace(String.format("\tpackages: %s", StringUtils.join(project.getPackages())));
            logger.trace(String.format("\tsources: %s", project.getSourcesDir()));
            logger.trace(String.format("\ttarget: %s", project.getTargetDir()));
            logger.trace("}");
        }

        return projects;
    }

    private List<String> getCommaSeparatedValues(String value) {
        List<String> values = new ArrayList<>();

        if (StringUtils.isNotEmpty(value)) {
            String[] split = value.split(",");
            for (String s : split) {
                values.add(s.trim());
            }
        }

        return values;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader cr = new ClassReader(classfileBuffer);
        DynamicClassInfo classInfo = new DynamicClassInfo(cr);
        className = ClassNameUtils.toJavaName(classInfo.getName());
        String classInternalName = ClassNameUtils.toInternalName(className);

        if (classInternalName.startsWith("java/lang")) {
            return null;
        }

        if (classInternalName.startsWith("applica/puccj") && !classInternalName.startsWith("applica/puccj/tests")) {
            return null;
        }

        if (!isSystemClassLoader(loader)) {
            return null;
        }

        if (TypeUtils.isPackageAllowed(DynamicRuntime.instance().getAllowedPackages(), className)) {
            if (className.endsWith(SuperType.SUFFIX)) {
                return null;
            }

            if (className.contains("EnhancerByCGLIB")) {
                return null;
            }

            try {
                Transformer transformer = new Transformer(DynamicRuntime.instance().getAllowedPackages());
                byte[] newBytes = transformer.transform(classfileBuffer, classInternalName);

                return newBytes;
            } catch (Throwable t) {
                t.printStackTrace();
            }

        } else {
            if (className.equals(RefreshConstructorParameter.class.getName())) {
                return null;
            }

            if (!classInfo.isInterf4ce() && classInfo.isPublicClass()) {
                ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
                RefreshConstructorClassVisitor cv = new RefreshConstructorClassVisitor(className, DynamicRuntime.instance().getAllowedPackages(), cw, classInfo);
                cr.accept(cv, 0);

                logger.trace(String.format("Created refresh constructor for class: %s", classInternalName));

                byte[] bytes = cw.toByteArray();

                /*if (Transformer.traceEnabled) {
                    Transformer.traceClass(ClassNameUtils.toInternalName(className), bytes);
                }*/

                return bytes;
            }
        }

        return null;
    }

    private boolean isSystemClassLoader(ClassLoader loader) {
        if (loader == null) {
            return false;
        }

        if (loader == ClassLoader.getSystemClassLoader()) {
            return true;
        }

        if (loader.getParent() != null) {
            return isSystemClassLoader(loader.getParent());
        }

        return false;
    }

}
