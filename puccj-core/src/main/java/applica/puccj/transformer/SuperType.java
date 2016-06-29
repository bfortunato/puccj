package applica.puccj.transformer;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SuperType {

    public static final String SUFFIX = "__puccj_superType";

    private String internalName;
    private Class<?> type;

    public SuperType(String internalName, Class<?> type) {
        this.internalName = internalName;
        this.type = type;
    }

    public SuperType() {
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
