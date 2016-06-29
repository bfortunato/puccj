package applica.puccj.tests.consoleapp.methodsign;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class MethodSignTest {

    public void doIt() {
        MethodSignService svc = new MethodSignService();
        Param1 p1 = new Param1();
        Param2 p2 = new Param2();
        svc.print(p1);
        svc.print(p2);
    }

}
