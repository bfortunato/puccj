package applica.puccj.tests.consoleapp.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SpringTest {

    private ApplicationContext applicationContext;

    public void doIt() {
        System.out.println("Spring test edited");

        if (applicationContext == null) {
            applicationContext = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        }

        SpringService springService = applicationContext.getBean(SpringService.class);
        springService.print();

        SpringController springController = applicationContext.getBean(SpringController.class);
        springController.print();

        SpringFacade springFacade = applicationContext.getBean(SpringFacade.class);
        springFacade.facade();

    }

}
