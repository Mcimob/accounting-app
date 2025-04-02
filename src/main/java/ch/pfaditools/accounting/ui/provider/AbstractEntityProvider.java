package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.BaseService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractEntityProvider<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends AbstractBackEndDataProvider<T, F> {

    private final BaseService<T, F> service;

    public AbstractEntityProvider(BaseService<T, F> service) {
        this.service = service;
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, F> query) {
        Optional<F> filter = query.getFilter();
        ServiceResponse<Page<T>> response =
                service.fetch(PageRequest.of(query.getPage(), query.getPageSize()),
                        filter.orElseGet(this::getFilter));

        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            return Stream.empty();
        }

        return response.getEntity().get().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<T, F> query) {
        Optional<F> filter = query.getFilter();
        ServiceResponse<Long> response = service.count(filter.orElseGet(this::getFilter));

        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            return 0;
        }
        return response.getEntity().get().intValue();
    }

    protected abstract F getFilter();

}
