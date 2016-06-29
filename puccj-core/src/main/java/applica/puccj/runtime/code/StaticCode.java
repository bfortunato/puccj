package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.StaticPointer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class StaticCode extends Code {

    public StaticCode(RuntimeType type) {
        super(type);
    }

    public StaticPointer pointer(final String name, final String desc) {
        StaticPointer pointer = (StaticPointer) CollectionUtils.find(pointers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                StaticPointer p = (StaticPointer) o;
                return p.getOwner().equals(type) &&
                        p.getName().equals(name) &&
                        p.getDesc().equals(desc);
            }
        });

        if (pointer == null) {
            pointer = new StaticPointer();
            pointer.setOwner(type);
            pointer.setName(name);
            pointer.setDesc(desc);

            pointers.add(pointer);
        }

        return pointer;
    }
}
