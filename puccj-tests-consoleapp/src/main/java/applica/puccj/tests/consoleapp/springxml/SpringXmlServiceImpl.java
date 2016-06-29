package applica.puccj.tests.consoleapp.springxml;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SpringXmlServiceImpl implements SpringXmlService {

    @Autowired
    private SpringXmlComponent component;

    @Override
    public void print() {
        System.out.println("SpringXmlServiceImpl");
        component.compose();
    }
}
