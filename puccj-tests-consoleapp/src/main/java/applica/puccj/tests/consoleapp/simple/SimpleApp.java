package applica.puccj.tests.consoleapp.simple;

import applica.puccj.tests.consoleapp.simple.beans.SimpleBean;

/**
 * Created by bimbobruno on 27/01/16.
 */
public class SimpleApp {

    public static void main(String[] args) {
        while (true) {
            run();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void run() {
        SimpleBean b;
        try {
            b = new SimpleBean();
            b.print();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
