package applica.puccj.tests.consoleapp.spring;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bimbobruno on 11/02/16.
 */
public class SpringFacade {

    @Autowired
    private SpringService springService;

    public void facade() {
        System.out.println("Facade");
        springService.print();
    }

}
