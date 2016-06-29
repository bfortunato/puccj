package applica.puccj.tests.consoleapp.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by bimbobruno on 31/10/15.
 */
@Configuration()
public class SpringConfiguration {

    @Bean
    public SpringController springController() {
        return new SpringControllerImpl();
    }

    @Bean
    public SpringService springService() {
        return new SpringServiceImpl();
    }

    @Bean
    public SpringFacade springFacade() {
        return new SpringFacade();
    }

}
