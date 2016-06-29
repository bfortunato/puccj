package applica.puccj.tests.consoleapp.inheritance;

/**
 * Created by bimbobruno on 07/11/15.
 */
public class Bruno extends Person {
    public Bruno() {
        super(new Home());
    }

    @Override
    public void print() {
        System.out.println("Print from bruno");
        super.print();
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalized");


        super.finalize();
    }
}
