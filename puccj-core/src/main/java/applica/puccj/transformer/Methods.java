package applica.puccj.transformer;

import applica.puccj.utils.ClassNameUtils;

/**
 * Created by bimbobruno on 28/10/15.
 */
public class Methods {

    public static final String INSTANCE_ID_FIELD = "__puccj_instanceId";
    public static final String INSTANCE_ID_GETTER = "__puccj_getInstanceId";
    public static final String INSTANCE_ID_SETTER = "__puccj_setInstanceId";

    public static String CONSTRUCTOR(String classInternalName) { return String.format("%s__puccj_init", ClassNameUtils.toInternalName(classInternalName).replace("/", "_")); }

    public static final String LOOKUP = "__puccj_lookup";
    public static final String CLASSNAME = "__puccj_classname";
//    public static final String PROXY_METHOD_SUFFIX = "__puccj_proxy";
    public static final String DYNAMIC_METHOD_SUFFIX = "__puccj_dynamic";
    public static final String ORIGINAL_METHOD_SUFFIX = "__puccj_original";
    public static final String PASS = "__puccj_pass";
}
