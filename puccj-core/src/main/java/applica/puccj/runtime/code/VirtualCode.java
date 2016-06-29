package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.VirtualPointer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class VirtualCode extends Code {

    public VirtualCode(RuntimeType type) {
        super(type);
    }

    public VirtualPointer pointer(final String name, final String desc) {
        VirtualPointer pointer = (VirtualPointer) CollectionUtils.find(pointers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                VirtualPointer p = (VirtualPointer) o;
                return p.getOwner().equals(type) &&
                        p.getName().equals(name) &&
                        p.getDesc().equals(desc);
            }
        });

        if (pointer == null) {
            pointer = new VirtualPointer();
            pointer.setOwner(type);
            pointer.setName(name);
            pointer.setDesc(desc);

            pointers.add(pointer);
        }

        return pointer;
    }
}
