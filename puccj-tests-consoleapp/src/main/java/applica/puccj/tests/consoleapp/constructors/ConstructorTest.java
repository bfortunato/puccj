package applica.puccj.tests.consoleapp.constructors;

/**
 * Created by bimbobruno on 27/10/15.
 */
public class ConstructorTest {

    public void doIt() {
        Home home = new Home();
        Person person = new Person(home);
        System.out.println(person.getFirstName());
    }

}
