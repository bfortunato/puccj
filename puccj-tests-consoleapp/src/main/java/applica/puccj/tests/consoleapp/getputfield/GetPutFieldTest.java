package applica.puccj.tests.consoleapp.getputfield;

/**
 * Created by bimbobruno on 23/10/15.
 */
public class GetPutFieldTest {

    public void doIt() {
        DynamicClass c;
        try {
            c = new DynamicClass();
            c.getClass();
            c.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
