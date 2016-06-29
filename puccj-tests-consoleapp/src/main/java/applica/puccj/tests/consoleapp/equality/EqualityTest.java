package applica.puccj.tests.consoleapp.equality;

/**
 * Created by bimbobruno on 19/01/16.
 */
public class EqualityTest {

    private String value = "value";

    public void doIt() {
        equals("value");
    }

    @Override
    public boolean equals(Object obj) {
        String nv = this.value;
        return false;
    }
}
