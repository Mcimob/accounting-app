package ch.pfaditools.accounting;

import ch.pfaditools.accounting.backend.repository.BaseRepositoryImpl;
import ch.pfaditools.accounting.backend.repository.CustomRepositoryFactoryBean;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@EntityScan(basePackages = "ch.pfaditools.accounting.model.entity")
@EnableJpaRepositories(
        basePackageClasses = BaseRepositoryImpl.class,
        repositoryFactoryBeanClass = CustomRepositoryFactoryBean.class)
@SpringBootApplication
@Theme(value = "accounting-app")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
