package applica.puccj.tests.consoleapp.inheritance;

/**
 * Created by bimbobruno on 27/10/15.
 */
public class InheritanceTest {


    public void doIt() {
        Home home = new Home();
        Person person = new Person(home);
        Bruno bruno = new Bruno();
        bruno.print();

    }

}
