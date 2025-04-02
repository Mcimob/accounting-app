package ch.pfaditools.accounting.backend.dao.impl;

import ch.pfaditools.accounting.backend.dao.exception.DaoException;
import ch.pfaditools.accounting.backend.repository.BaseRepository;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import ch.pfaditools.accounting.model.loadtype.BaseLoadType;
import ch.pfaditools.accounting.model.loadtype.HasLoadType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
abstract class BaseDaoImplTest<T extends AbstractEntity, F extends AbstractFilter<T>> {

    @Mock
    private BaseRepository<T, F> repository;

    @InjectMocks
    private BaseDaoImpl<T, F> dao;

    @Test
    void fetchWithLoadTypeSuccess() throws DaoException {
        Page<T> page = new PageImpl<>(List.of(getEntity()));
        when(repository.findAll(any(), any(), any(HasLoadType.class)))
                .thenReturn(page);

        Page<T> response = dao.fetch(Pageable.unpaged(), getFilter(), BaseLoadType.NONE);

        assertThat(response).isNotNull();
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

    protected abstract T getEntity();

    protected abstract F getFilter();
}
