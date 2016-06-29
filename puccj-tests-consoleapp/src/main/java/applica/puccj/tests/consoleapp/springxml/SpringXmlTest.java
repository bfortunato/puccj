package applica.puccj.tests.consoleapp.springxml;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by bimbobruno on 31/10/15.
 */
public class SpringXmlTest {

    private AbstractApplicationContext applicationContext;


    public void doIt() {
        if (applicationContext == null) {
            try {
                applicationContext = new ClassPathXmlApplicationContext("/spring.xml");
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }

        DefaultListableBeanFactory beanFactory = ((DefaultListableBeanFactory) applicationContext.getBeanFactory());
        SpringXmlService springXmlService = applicationContext.getBean(SpringXmlService.class);

        beanFactory.isAllowBeanDefinitionOverriding();
        springXmlService.print();

        SpringXmlController springXmlController = applicationContext.getBean(SpringXmlController.class);
        springXmlController.print();

    }

}
