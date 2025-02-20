package ch.pfaditools.accounting;

import ch.pfaditools.accounting.backend.repository.BaseRepositoryImpl;
import ch.pfaditools.accounting.backend.repository.CustomRepositoryFactoryBean;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@EnableJpaRepositories(
        basePackageClasses = BaseRepositoryImpl.class,
        repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class)
@SpringBootApplication
@Theme(value = "accounting-app")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
