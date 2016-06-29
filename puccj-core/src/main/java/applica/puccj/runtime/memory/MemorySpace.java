package applica.puccj.runtime.memory;

import applica.puccj.runtime.RuntimeType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bimbobruno on 11/10/15.
 */
public class MemorySpace {

    private List<InstanceMemory> instanceMemories = Collections.synchronizedList(new ArrayList<InstanceMemory>());
    private List<StaticMemory> staticMemories = Collections.synchronizedList(new ArrayList<StaticMemory>());
    private Log logger = LogFactory.getLog(getClass());

    public StaticMemory ofClass(final RuntimeType type) {
        StaticMemory staticMemory;

        synchronized (staticMemories) {
            staticMemory = (StaticMemory) CollectionUtils.find(staticMemories, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    StaticMemory m = ((StaticMemory) o);
                    return m.getType().equals(type);
                }
            });
        }

        if (staticMemory == null) {
            staticMemory = new StaticMemory(type);
            synchronized (staticMemories) {
                staticMemories.add(staticMemory);
            }
        }

        return staticMemory;
    }

    public InstanceMemory ofInstance(final long instanceId) {
        InstanceMemory instanceMemory;

        synchronized (instanceMemories) {
            instanceMemory = (InstanceMemory) CollectionUtils.find(instanceMemories, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    InstanceMemory m = ((InstanceMemory) o);
                    return m.getInstanceId() == instanceId;
                }
            });
        }

        if (instanceMemory == null) {
            instanceMemory = new InstanceMemory(instanceId);
            synchronized (instanceMemories) {
                instanceMemories.add(instanceMemory);
            }
        }

        return instanceMemory;
    }

    public void free(final long instanceId) {
        InstanceMemory instanceMemory;

        synchronized (instanceMemories) {
            instanceMemory = (InstanceMemory) CollectionUtils.find(instanceMemories, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    InstanceMemory m = ((InstanceMemory) o);
                    return m.getInstanceId() == instanceId;
                }
            });
        }

        if (instanceMemory != null) {
            instanceMemory.free();
        }

        instanceMemories.remove(instanceMemory);
    }


}
