package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.GetFieldPointer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class GetFieldCode extends Code {

    public GetFieldCode(RuntimeType type) {
        super(type);
    }

    public GetFieldPointer pointer(final String name, final String desc) {
        GetFieldPointer pointer = (GetFieldPointer) CollectionUtils.find(pointers, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                GetFieldPointer p = (GetFieldPointer) o;
                return p.getOwner().equals(type) &&
                        p.getName().equals(name) &&
                        p.getDesc().equals(desc);
            }
        });

        if (pointer == null) {
            pointer = new GetFieldPointer();
            pointer.setOwner(type);
            pointer.setName(name);
            pointer.setDesc(desc);

            pointers.add(pointer);
        }

        return pointer;
    }
}
