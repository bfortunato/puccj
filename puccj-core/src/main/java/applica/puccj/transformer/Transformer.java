package applica.puccj.transformer;

import applica.puccj.compiler.CompiledFile;
import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.utils.ClassNameUtils;
import applica.puccj.utils.TypeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class Transformer {

    public static boolean traceEnabled = false;
    public static String tracePath = null;
    public static boolean checkEnabled = false;

    private List<String> allowedPackages;
    private Log logger = LogFactory.getLog(getClass());

    public Transformer(List<String> allowedPackages) {
        this.allowedPackages = allowedPackages;
    }

    public List<TransformedFile> transform(List<CompiledFile> compiledFiles) {
        List<TransformedFile> transformations = new ArrayList<>();

        for (CompiledFile compiled : compiledFiles) {
            ClassReader cr = null;
            try {
                cr = new ClassReader(compiled.open());
            } catch (IOException e) {
                logger.error(String.format("Cannot transform class %s: %s", compiled.getClassInternalName(), e.getMessage()));
                continue;
            }

            //make class dynamic
            byte[] bytes = transform(cr, compiled.getClassInternalName());

            transformations.add(new TransformedFile(compiled.getClassInternalName(), bytes));

        }

        return transformations;
    }

    public byte[] transform(byte[] buffer, String classInternalName) {
        ClassReader cr = new ClassReader(buffer);

        return this.transform(cr, classInternalName);
    }

    public byte[] transform(ClassReader cr, String classInternalName) {
        if (traceEnabled) {
            try {
                ClassReader fcr = new ClassReader(cr.b);

                OutputStream out = System.out;

                if (tracePath != null) {

                    try {
                        String byteCodePath = String.format("%s/%s.original.bytecode", tracePath, classInternalName);
                        String classFilePath = String.format("%s/%s.original.class", tracePath, classInternalName);
                        FileUtils.forceMkdir(new File(FilenameUtils.getFullPath(byteCodePath)));
                        out = new FileOutputStream(byteCodePath);

                        FileUtils.writeByteArrayToFile(new File(classFilePath), cr.b);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                PrintWriter printWriter = new PrintWriter(out);
                TraceClassVisitor tcv = new TraceClassVisitor(printWriter);
                fcr.accept(tcv, 0);

                if (tracePath != null) {
                    IOUtils.closeQuietly(out);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        DynamicClassInfo classInfo = new DynamicClassInfo(cr);

        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new DynamicClassVisitor(classInternalName, allowedPackages, cw, classInfo);
        cr.accept(cv, 0);

        byte[] bytes = cw.toByteArray();

        if (checkEnabled) {
            PrintWriter printWriter = new PrintWriter(System.out);
            ClassReader fcr = new ClassReader(bytes);
            CheckClassAdapter.verify(fcr, new CompiledClassLoader(allowedPackages, Thread.currentThread().getContextClassLoader()), false, printWriter);
        }

        //create refresh constructor for non interfaces
        if (!classInfo.isInterf4ce()) {
            ClassReader rccr = new ClassReader(bytes);
            ClassWriter rccw = new ClassWriter(cr, 0);
            ClassVisitor rccv = new RefreshConstructorClassVisitor(
                    classInternalName,
                    allowedPackages,
                    rccw,
                    classInfo
            );
            rccr.accept(rccv, 0);

            bytes = rccw.toByteArray();
        }

        //add lookup static method to class
        if (!classInfo.isInterf4ce()) {
            ClassReader lcr = new ClassReader(bytes);
            ClassWriter lcw = new ClassWriter(lcr, 0);
            LookupClassVisitor lcv = new LookupClassVisitor(lcw);
            lcr.accept(lcv, 0);

            bytes = lcw.toByteArray();

            //add get class name method
            ClassReader cnr = new ClassReader(bytes);
            ClassWriter cnw = new ClassWriter(cnr, 0);
            ClassNameClassVisitor cnv = new ClassNameClassVisitor(classInternalName, cnw);
            cnr.accept(cnv, 0);

            bytes = cnw.toByteArray();
        }

        if (checkEnabled) {
            PrintWriter printWriter = new PrintWriter(System.out);
            ClassReader fcr = new ClassReader(bytes);
            CheckClassAdapter.verify(fcr, new CompiledClassLoader(allowedPackages, Thread.currentThread().getContextClassLoader()), false, printWriter);
        }

        if (traceEnabled) {
            traceClass(classInternalName, bytes);
        }

        logger.trace(String.format("Class transformed: %s", classInternalName));

        List<String> duplicatedMethods = TypeUtils.findDuplicatedMethods(bytes);
        if (duplicatedMethods.size() > 0) {
            System.err.println("Duplicated methods found");
            for (String s : duplicatedMethods) {
                System.out.println(s);
            }

            throw new RuntimeException();
        }

        //invalidate all pointers for this class
        DynamicRuntime.instance().getCode().invalidateClass(classInternalName);

        return bytes;
    }

    public static void traceClass(String classInternalName, byte[] bytes) {
        try {
            ClassReader fcr = new ClassReader(bytes);

            OutputStream out = System.out;

            if (tracePath != null) {

                try {
                    String byteCodePath = String.format("%s/%s.bytecode", tracePath, classInternalName);
                    String classFilePath = String.format("%s/%s.class", tracePath, classInternalName);
                    FileUtils.forceMkdir(new File(FilenameUtils.getFullPath(byteCodePath)));
                    out = new FileOutputStream(byteCodePath);

                    FileUtils.writeByteArrayToFile(new File(classFilePath), bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            PrintWriter printWriter = new PrintWriter(out);
            TraceClassVisitor tcv = new TraceClassVisitor(printWriter);
            fcr.accept(tcv, 0);

            if (tracePath != null) {
                IOUtils.closeQuietly(out);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Class<?> generateSuperType(String classInternalName) {
        return generateSuperType(classInternalName, Thread.currentThread().getContextClassLoader());
    }

    public Class<?> generateSuperType(String classInternalName, ClassLoader classLoader) {
        ClassWriter stw = new ClassWriter(0);
        String superTypeInternalName = String.format("%s%s", classInternalName, SuperType.SUFFIX);
        int access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE;

        stw.visit(Opcodes.V1_7, access, superTypeInternalName, null, "java/lang/Object", null);
        stw.visitEnd();
        byte[] bytes = stw.toByteArray();

        Class superClass = TypeUtils.defineClass(ClassNameUtils.toJavaName(superTypeInternalName), bytes);

        if (traceEnabled) {
            ClassReader fcr = new ClassReader(bytes);

            OutputStream out = System.out;

            if (tracePath != null) {

                try {
                    String byteCodePath = String.format("%s/%s.bytecode", tracePath, superTypeInternalName);
                    String classFilePath = String.format("%s/%s.class", tracePath, superTypeInternalName);
                    FileUtils.forceMkdir(new File(FilenameUtils.getFullPath(byteCodePath)));
                    out = new FileOutputStream(byteCodePath);

                    FileUtils.writeByteArrayToFile(new File(classFilePath), bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            PrintWriter printWriter = new PrintWriter(out);
            TraceClassVisitor tcv = new TraceClassVisitor(printWriter);
            fcr.accept(tcv, 0);

            if (tracePath != null) {
                IOUtils.closeQuietly(out);
            }
        }

        return superClass;
    }

}
