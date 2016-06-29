package applica.puccj.tests.consoleapp.cglib;

/**
 * Created by bimbobruno on 27/10/15.
 */
public class CGLibTest {

    public void doIt() {
        CGLibService service = EnhancedCGLibServiceFactory.create(null);

        String value = service.getValue();
        service.print();

        System.out.println(value);

    }

}
