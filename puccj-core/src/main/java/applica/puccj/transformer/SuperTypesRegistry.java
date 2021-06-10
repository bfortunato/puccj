package applica.puccj.transformer;

import applica.puccj.runtime.DynamicRuntime;
import applica.puccj.utils.ClassNameUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SuperTypesRegistry {

    private Log logger = LogFactory.getLog(getClass());

    private List<SuperType> superTypes = new ArrayList<>();

    private static SuperTypesRegistry s_instance;

    public static SuperTypesRegistry instance() {
        if (s_instance == null) {
            s_instance = new SuperTypesRegistry();
            return s_instance;
        }

        return s_instance;
    }

    private SuperTypesRegistry() {}

    public SuperType getSuperType(final String internalName) {
        final String checkedInternalName = ClassNameUtils.toInternalName(internalName);
        SuperType superType = ((SuperType) CollectionUtils.find(superTypes, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                SuperType s = (SuperType) o;
                return checkedInternalName.equals(s.getInternalName()) || ClassNameUtils.toInternalName(s.getType().getName()).equals(checkedInternalName);
            }
        }));

        if (superType == null) {
            Transformer transformer = new Transformer(DynamicRuntime.instance().getAllowedPackages());
            Class<?> superTypeClass = transformer.generateSuperType(internalName);

            superType = new SuperType(internalName, superTypeClass);
            superTypes.add(superType);

            logger.info(String.format("New super type created: %s", internalName));
        }

        return superType;
    }
}
