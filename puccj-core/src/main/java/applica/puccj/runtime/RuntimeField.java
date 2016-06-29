package applica.puccj.runtime;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class RuntimeField {

    private String name;
    private RuntimeType type;

    public RuntimeField(String name, RuntimeType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RuntimeType getType() {
        return type;
    }

    public void setType(RuntimeType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        RuntimeField other = ((RuntimeField) obj);
        if (other == null) {
            return false;
        }

        return type.equals(other.getType()) && name.equals(other.getName());
    }

    @Override
    public String toString() {
        return String.format("%s.%s", type.toString(), name);
    }
}
