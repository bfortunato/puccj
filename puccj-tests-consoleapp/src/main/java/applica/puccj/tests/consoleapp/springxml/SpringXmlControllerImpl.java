package applica.puccj.tests.consoleapp.springxml;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SpringXmlControllerImpl implements SpringXmlController {

    @Autowired
    private SpringXmlService springXmlService;

    @Autowired
    private SpringXmlFacade facade;



    @Override
    public void print() {
        System.out.println("SpringXMLControllerImpl");

        facade.facade();

        springXmlService.print();
    }
}
