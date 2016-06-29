package applica.puccj.linkage.arguments;

import java.util.Arrays;

/**
 * Created by bimbobruno on 11/10/15.
 */
public abstract class InvokeWithMetaArguments extends InvokeArguments {

    private Object[] meta;

    public InvokeWithMetaArguments(Object[] args) {
        super(args);
    }

    protected abstract int getMetaSize();

    @Override
    protected int getArgsStartIndex() {
        int start = hasInstance() ? 1 : 0;
        return start;
    }

    @Override
    protected int getArgsEndIndex() {
        int size = objects.length - getMetaSize();
        if (hasInstance()) {
            size--;
        }

        return getArgsStartIndex() + size;
    }

    public Object[] getMeta() {
        if (meta == null) {
            meta = Arrays.copyOfRange(objects, getArgsEndIndex(), objects.length);
        }
        return meta;
    }
}
