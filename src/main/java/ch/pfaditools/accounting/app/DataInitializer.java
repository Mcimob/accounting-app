package ch.pfaditools.accounting.app;

import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.backend.service.UserService;
import ch.pfaditools.accounting.model.entity.UserEntity;
import ch.pfaditools.accounting.model.filter.UserEntityFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static ch.pfaditools.accounting.security.SecurityConstants.ROLE_ADMIN_STRING;

@Configuration
public class DataInitializer {

    private final AppConfiguration appConfiguration;

    public DataInitializer(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Bean
    CommandLineRunner init(UserService userService, PasswordEncoder passwordEncoder) {
        return args -> {
            UserEntityFilter filter = new UserEntityFilter();
            filter.setUsername(appConfiguration.getAdminUsername());
            filter.setExactMatch(true);
            ServiceResponse<UserEntity> response = userService.fetchOne(filter);

            if (response.getEntity().isEmpty()) {
                UserEntity admin = new UserEntity();
                admin.setUsername(appConfiguration.getAdminUsername());
                admin.setPassword(passwordEncoder.encode(appConfiguration.getAdminPassword()));
                admin.setRoles(Set.of(ROLE_ADMIN_STRING));
                admin.updateCreateModifyFields("INIT");
                userService.save(admin);

                System.out.println("Admin user created: " + appConfiguration.getAdminUsername());
            }
        };
    }
}
