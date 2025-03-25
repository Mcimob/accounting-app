package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.BaseService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.AbstractEntity;
import ch.pfaditools.accounting.model.filter.AbstractFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public abstract class AbstractEntityStringProvider<T extends AbstractEntity, F extends AbstractFilter<T>>
        extends AbstractBackEndDataProvider<T, String> {

    private final transient BaseService<T, F> service;

    public AbstractEntityStringProvider(BaseService<T, F> service) {
        this.service = service;
    }

    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, String> query) {
        Optional<String> filter = query.getFilter();
        F entityFilter = getFilter();
        filter.ifPresent(value ->
                getSetters().forEach(setter ->
                        setter.accept(entityFilter, value)));

        ServiceResponse<Page<T>> response =
                service.fetch(PageRequest.of(query.getPage(), query.getPageSize()), entityFilter);
        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            return Stream.empty();
        }

        return response.getEntity().get().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<T, String> query) {
        return (int) fetchFromBackEnd(query).count();
    }

    protected abstract F getFilter();

    protected abstract List<BiConsumer<F, String>> getSetters();
}
