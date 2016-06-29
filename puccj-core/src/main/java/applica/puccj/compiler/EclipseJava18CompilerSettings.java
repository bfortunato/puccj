package applica.puccj.compiler;

import org.apache.commons.jci.compilers.JavaCompilerSettings;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class EclipseJava18CompilerSettings extends JavaCompilerSettings {

    final private Map<String, String> defaultEclipseSettings = new HashMap<String, String>();

    public EclipseJava18CompilerSettings() {
        defaultEclipseSettings.put(CompilerOptions.OPTION_LineNumberAttribute, CompilerOptions.GENERATE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_SourceFileAttribute, CompilerOptions.GENERATE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_ReportUnusedImport, CompilerOptions.IGNORE);
        defaultEclipseSettings.put(CompilerOptions.OPTION_LocalVariableAttribute, CompilerOptions.GENERATE);
    }

    public EclipseJava18CompilerSettings(final JavaCompilerSettings pSettings) {
        super(pSettings);

        if (pSettings instanceof EclipseJava18CompilerSettings) {
            defaultEclipseSettings.putAll(((EclipseJava18CompilerSettings) pSettings).toNativeSettings());
        }
    }

    public EclipseJava18CompilerSettings(final Map<String, String> pMap) {
        defaultEclipseSettings.putAll(pMap);
    }

    private static Map<String, String> nativeVersions = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            put("1.1", CompilerOptions.VERSION_1_1);
            put("1.2", CompilerOptions.VERSION_1_2);
            put("1.3", CompilerOptions.VERSION_1_3);
            put("1.4", CompilerOptions.VERSION_1_4);
            put("1.5", CompilerOptions.VERSION_1_5);
            put("1.6", CompilerOptions.VERSION_1_6);
            put("1.7", CompilerOptions.VERSION_1_7);
            put("1.8", CompilerOptions.VERSION_1_8);
        }
    };

    private String toNativeVersion(final String pVersion) {
        final String nativeVersion = nativeVersions.get(pVersion);

        if (nativeVersion == null) {
            throw new RuntimeException("unknown version " + pVersion);
        }

        return nativeVersion;
    }

    Map<String, String> toNativeSettings() {
        final Map<String, String> map = new HashMap<String, String>(defaultEclipseSettings);

        map.put(CompilerOptions.OPTION_SuppressWarnings, isWarnings() ? CompilerOptions.GENERATE : CompilerOptions.DO_NOT_GENERATE);
        map.put(CompilerOptions.OPTION_ReportDeprecation, isDeprecations() ? CompilerOptions.GENERATE : CompilerOptions.DO_NOT_GENERATE);
        map.put(CompilerOptions.OPTION_TargetPlatform, toNativeVersion(getTargetVersion()));
        map.put(CompilerOptions.OPTION_Source, toNativeVersion(getSourceVersion()));
        map.put(CompilerOptions.OPTION_Encoding, getSourceEncoding());

        return map;
    }

    @Override
    public String toString() {
        return toNativeSettings().toString();
    }

}