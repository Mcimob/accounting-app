package ch.pfaditools.accounting.ui.provider;

import ch.pfaditools.accounting.backend.service.ReceiptService;
import ch.pfaditools.accounting.model.entity.ReceiptEntity;
import ch.pfaditools.accounting.model.filter.ReceiptEntityFilter;
import ch.pfaditools.accounting.security.SecurityUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiConsumer;

@Component
public class ReceiptStringProvider extends AbstractEntityStringProvider<ReceiptEntity, ReceiptEntityFilter> {

    private ReceiptEntityFilter filter;

    public ReceiptStringProvider(ReceiptService service) {
        super(service);
        setupFilter();
    }

    private void setupFilter() {
        ReceiptEntityFilter filter = new ReceiptEntityFilter();
        filter.setGroup(SecurityUtils.getAuthenticatedUserGroup());
        this.filter = filter;
    }

    @Override
    public ReceiptEntityFilter getFilter() {
        return filter;
    }

    @Override
    protected List<BiConsumer<ReceiptEntityFilter, String>> getSetters() {
        return List.of(ReceiptEntityFilter::setUserInput);
    }
}
