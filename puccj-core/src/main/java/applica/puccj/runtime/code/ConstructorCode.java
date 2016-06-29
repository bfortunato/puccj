package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.ConstructorPointer;
import applica.puccj.transformer.Methods;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class ConstructorCode extends Code {

    public ConstructorCode(RuntimeType type) {
        super(type);
    }

    public ConstructorPointer pointer(final String desc) {
        ConstructorPointer pointer = (ConstructorPointer) CollectionUtils.find(pointers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                ConstructorPointer p = (ConstructorPointer) o;
                return p.getOwner().equals(type) &&
                        p.getName().equals(Methods.CONSTRUCTOR(getType().getClassInternalName())) &&
                        p.getDesc().equals(desc);
            }
        });

        if (pointer == null) {
            pointer = new ConstructorPointer();
            pointer.setOwner(type);
            pointer.setName(Methods.CONSTRUCTOR(getType().getClassInternalName()));
            pointer.setDesc(desc);

            pointers.add(pointer);
        }

        return pointer;
    }
}
