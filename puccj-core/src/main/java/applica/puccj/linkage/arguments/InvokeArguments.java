package applica.puccj.linkage.arguments;

import java.util.Arrays;

/**
 * Created by bimbobruno on 11/10/15.
 */
public abstract class InvokeArguments {

    protected Object[] objects;
    protected Object[] args;

    public InvokeArguments(Object[] objects) {
        this.objects = objects;
    }

    protected abstract int getArgsStartIndex();
    protected abstract int getArgsEndIndex();
    protected abstract boolean hasInstance();

    public Object getInstance() {
        if (!hasInstance()) {
            throw new RuntimeException("Arguments are configured without instance");
        }
        return objects[0];
    }

    public Object[] getArgs() {
        if (args == null) {
            args = Arrays.copyOfRange(objects, getArgsStartIndex(), getArgsEndIndex());
        }

        return args;
    }
}
