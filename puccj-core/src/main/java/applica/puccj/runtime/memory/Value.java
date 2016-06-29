package applica.puccj.runtime.memory;

import applica.puccj.runtime.RuntimeField;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class Value {

    private RuntimeField field;
    private Object val;

    public Value(RuntimeField field, Object val) {
        this.field = field;
        this.val = val;
    }

    public RuntimeField getField() {
        return field;
    }

    public void setField(RuntimeField field) {
        this.field = field;
    }

    public Object getVal() {
        return val;
    }

    public void setVal(Object value) {
        this.val = value;
    }

    @Override
    public boolean equals(Object obj) {
        Value other = ((Value) obj);
        if (other == null) {
            return false;
        }

        return val.equals(other.val);
    }

    @Override
    public String toString() {
        return String.format("%s", val);
    }
}
