package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.InterfacePointer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class InterfaceCode extends Code {

    public InterfaceCode(RuntimeType type) {
        super(type);
    }

    public InterfacePointer pointer(final String name, final String desc) {
        InterfacePointer pointer = (InterfacePointer) CollectionUtils.find(pointers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                InterfacePointer p = (InterfacePointer) o;
                return p.getOwner().equals(type) &&
                        p.getName().equals(name) &&
                        p.getDesc().equals(desc);
            }
        });

        if (pointer == null) {
            pointer = new InterfacePointer();
            pointer.setOwner(type);
            pointer.setName(name);
            pointer.setDesc(desc);

            pointers.add(pointer);
        }

        return pointer;
    }
}
