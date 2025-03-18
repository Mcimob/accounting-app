package ch.pfaditools.accounting.ui.views.receipt;

import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.backend.service.ServiceResponse;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

@Component
public class ReceiptProvider extends AbstractBackEndDataProvider<ReceiptEntity, ReceiptEntityFilter> {

    private final ReceiptService service;

    public ReceiptProvider(ReceiptService service) {
        this.service = service;
    }

    @Override
    protected Stream<ReceiptEntity> fetchFromBackEnd(Query<ReceiptEntity, ReceiptEntityFilter> query) {
        Optional<ReceiptEntityFilter> filter = query.getFilter();
        ServiceResponse<Page<ReceiptEntity>> response =
                service.fetch(PageRequest.of(query.getPage(), query.getPageSize()),
                        filter.orElse(new ReceiptEntityFilter()));

        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            return Stream.empty();
        }

        return response.getEntity().get().stream();
    }

    @Override
    protected int sizeInBackEnd(Query<ReceiptEntity, ReceiptEntityFilter> query) {
        return (int) fetchFromBackEnd(query).count();
    }
}
