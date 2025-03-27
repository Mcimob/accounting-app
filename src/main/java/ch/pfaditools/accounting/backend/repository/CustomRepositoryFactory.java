package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class CustomRepositoryFactory extends JpaRepositoryFactory {

    public CustomRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(
            RepositoryInformation information, EntityManager entityManager) {

        JpaEntityInformation<?, Long> entityInformation = getEntityInformation(
                (Class<?>) information.getDomainType()
        );

        return new BaseRepositoryImpl<>(
                (JpaEntityInformation<? extends AbstractEntity, ?>) entityInformation,
                entityManager);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseRepositoryImpl.class;
    }
}

