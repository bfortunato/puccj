package applica.puccj.tests.consoleapp.constructors;

/**
 * Created by bimbobruno on 26/10/15.
 */
public class Person {


    String firstName;
    String lastName;
    Home home;

    public Person(Home home) {
        this.home = home;

        home.check();
    }

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String fullName() {
        return String.format("%s %s %s", firstName, lastName, home.getName());
    }
}
