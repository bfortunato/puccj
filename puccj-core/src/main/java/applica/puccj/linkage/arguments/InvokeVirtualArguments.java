package applica.puccj.linkage.arguments;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class InvokeVirtualArguments extends InvokeWithMetaArguments {

    public InvokeVirtualArguments(Object[] args) {
        super(args);
    }

    @Override
    protected int getMetaSize() {
        return 4;
    }

    @Override
    protected boolean hasInstance() {
        return true;
    }

    public String getOwner() {
        return (String) getMeta()[0];
    }

    public String getName() {
        return (String) getMeta()[1];
    }

    public String getDesc() {
        return (String) getMeta()[2];
    }

    public boolean isItf() {
        return (boolean) getMeta()[3];
    }
}
