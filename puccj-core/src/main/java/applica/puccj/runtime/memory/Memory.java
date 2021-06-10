package applica.puccj.runtime.memory;

import applica.puccj.runtime.RuntimeField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class Memory {

    private Log logger = LogFactory.getLog(getClass());

    private List<Value> values = new ArrayList<>();

    public boolean hasValue(RuntimeField field) {
        for (Value v : values) {
            if (v.getField().equals(field)) {
                return true;
            }
        }

        return false;
    }

    public Value getValue(RuntimeField field) {
        for (Value v : values) {
            if (v.getField().equals(field)) {
                return v;
            }
        }

        return null;
    }

    public Object get(RuntimeField field) {
        Value value = getOrCreateValue(field);

        logger.info(String.format("Getting value %s: %s", field.toString(), value));

        return value.getVal();
    }

    public void put(RuntimeField field, Object val) {
        Value value = getOrCreateValue(field);

        logger.info(String.format("Setting value %s: %s", field.toString(), val));

        value.setVal(val);
    }

    private Value getOrCreateValue(RuntimeField field) {
        Value value = null;
        for (Value v : values) {
            if (v.getField().equals(field)) {
                value = v;
                break;
            }
        }

        if (value == null) {
            value = new Value(field, null);
            values.add(value);
        }

        return value;
    }


    public void free() {
        values.clear();
    }


    public void dump(PrintStream out) {
        if (out == null) {
            out = System.out;
        }

        out.println("{");

        for (Value v : values) {
            out.println(String.format("\t%s = %s", v.getField(), v.getVal()));
        }

        out.println("}");
    }
}
