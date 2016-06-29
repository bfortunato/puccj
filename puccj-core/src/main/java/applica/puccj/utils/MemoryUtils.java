package applica.puccj.utils;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.runtime.__PuccjObject;

/**
 * Created by bimbobruno on 19/01/16.
 */
public class MemoryUtils {

    public static void freeInstance(__PuccjObject instance) {
        DynamicRuntime.instance().getMemory().free(instance.__puccj_getInstanceId());
    }

}
