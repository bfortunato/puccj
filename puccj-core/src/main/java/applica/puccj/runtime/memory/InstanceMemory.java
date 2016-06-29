package applica.puccj.runtime.memory;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class InstanceMemory extends Memory {
    private long instance;

    public InstanceMemory(long instance) {
        this.instance = instance;
    }

    public long getInstanceId() {
        return instance;
    }

    public void setInstance(long instance) {
        this.instance = instance;
    }

}
