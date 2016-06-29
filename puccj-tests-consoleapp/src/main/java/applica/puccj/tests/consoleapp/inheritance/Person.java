package applica.puccj.tests.consoleapp.inheritance;

/**
 * Created by bimbobruno on 26/10/15.
 */
public class Person {

    Home home;

    public Person(Home home) {
        this.home = home;
        home.check();
    }

    public void print() {
        System.out.println("Ciao da person");
    }

}
