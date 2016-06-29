package applica.puccj.tests.consoleapp.spring;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SpringControllerImpl implements SpringController {

    @Autowired
    private SpringService springService;

    @Override
    public void print() {
        System.out.println("Controller");
        springService.print();
    }
}
