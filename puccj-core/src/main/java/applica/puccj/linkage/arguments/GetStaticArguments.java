package applica.puccj.linkage.arguments;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class GetStaticArguments extends InvokeWithMetaArguments {

    public GetStaticArguments(Object[] args) {
        super(args);
    }

    @Override
    protected int getMetaSize() {
        return 3;
    }

    @Override
    protected boolean hasInstance() {
        return false;
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

}
