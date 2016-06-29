package applica.puccj.runtime.code;

import applica.puccj.runtime.RuntimeType;
import applica.puccj.runtime.code.pointers.Pointer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 14/10/15.
 */
public class Code {
    protected RuntimeType type;
    protected List<Pointer> pointers = new ArrayList<>();

    public List<Pointer> getPointers() {
        return pointers;
    }

    public void setPointers(List<Pointer> pointers) {
        this.pointers = pointers;
    }


    public Code(RuntimeType type) {
        this.type = type;
    }

    public RuntimeType getType() {
        return type;
    }

    public void setType(RuntimeType type) {
        this.type = type;
    }

    public void invalidate() {
        for (Pointer p : pointers) {
            p.invalidate();
        }
    }
}
