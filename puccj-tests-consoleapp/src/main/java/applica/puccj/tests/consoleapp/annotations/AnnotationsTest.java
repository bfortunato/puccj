package applica.puccj.tests.consoleapp.annotations;


/**
 * Created by bimbobruno on 19/01/16.
 */
public class AnnotationsTest {

    private String value = "value";

    public void doIt() {
        equals("value");
    }

    @Deprecated
    public void annotatedMethod(@Deprecated String ciao) {

    }

    @Override
    public boolean equals(Object obj) {
        String nv = this.value;
        return false;
    }
}
