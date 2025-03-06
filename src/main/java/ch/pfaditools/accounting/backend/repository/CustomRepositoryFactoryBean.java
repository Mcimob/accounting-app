package ch.pfaditools.accounting.backend.repository;

import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

public class CustomRepositoryFactoryBean<
        R extends BaseRepository<T, F>,
        T extends AbstractEntity,
        F extends AbstractFilter<T>>
        extends JpaRepositoryFactoryBean<R, T, Long> {

    public CustomRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected JpaRepositoryFactory createRepositoryFactory(EntityManager entityManager) {
        return new CustomRepositoryFactory(entityManager);
    }
}
