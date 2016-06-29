package applica.puccj.tests.consoleapp.springxml;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bimbobruno on 11/02/16.
 */
public class SpringXmlFacade {

    @Autowired
    private SpringXmlService springXmlService;

    public void facade() {
        System.out.println("XMLFacade modified");
        springXmlService.print();
    }

}
