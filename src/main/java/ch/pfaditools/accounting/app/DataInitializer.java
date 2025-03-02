package ch.pfaditools.accounting.app;

import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.role}")
    private String adminRole;

    @Bean
    CommandLineRunner init(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            UserEntityFilter filter = new UserEntityFilter();
            filter.setUsername(adminUsername);
            ServiceResponse<UserEntity> response = userService.fetchOne(filter);

            if (response.getEntity().isEmpty()) {
                UserEntity admin = new UserEntity();
                admin.setUsername(adminUsername);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRoles(Set.of(adminRole));
                admin.updateCreateModifyFields("INIT");
                userService.save(admin);

                System.out.println("Admin user created: " + adminUsername);
            }
        };
    }
}
