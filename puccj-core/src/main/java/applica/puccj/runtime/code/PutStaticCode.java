package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.PutStaticPointer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class PutStaticCode extends Code {

    public PutStaticCode(RuntimeType type) {
        super(type);
    }

    public PutStaticPointer pointer(final String name, final String desc) {
        PutStaticPointer pointer = (PutStaticPointer) CollectionUtils.find(pointers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                PutStaticPointer p = (PutStaticPointer) o;
                return p.getOwner().equals(type) &&
                        p.getName().equals(name) &&
                        p.getDesc().equals(desc);
            }
        });

        if (pointer == null) {
            pointer = new PutStaticPointer();
            pointer.setOwner(type);
            pointer.setName(name);
            pointer.setDesc(desc);

            pointers.add(pointer);
        }

        return pointer;
    }
}
