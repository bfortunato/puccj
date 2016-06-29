package applica.puccj.runtime.classes;

/**
 * Created by bimbobruno on 11/10/15.
 */
public interface ClassChangeListener {

    void onClassChange(String internalName, Class<?> newClass);
    int getInvokePriority();

}