package applica.puccj.runtime.memory;

import applica.puccj.runtime.RuntimeType;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class StaticMemory extends Memory {

    private RuntimeType type;

    public StaticMemory(RuntimeType type) {
        this.type = type;
    }

    public RuntimeType getType() {
        return type;
    }

    public void setType(RuntimeType type) {
        this.type = type;
    }

}
