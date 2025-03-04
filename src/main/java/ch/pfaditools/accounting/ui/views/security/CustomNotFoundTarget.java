package ch.pfaditools.accounting.ui.views.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.RouteNotFoundError;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serial;

import static ch.pfaditools.accounting.ui.ViewConstants.ROUTE_ACCESS_DENIED;

public class CustomNotFoundTarget extends RouteNotFoundError {

    @Serial
    private static final long serialVersionUID = 3337229943239284836L;

    @Override
    public int setErrorParameter(final BeforeEnterEvent event, final ErrorParameter<NotFoundException> parameter) {
        event.forwardTo(ROUTE_ACCESS_DENIED);
        return HttpServletResponse.SC_NOT_FOUND;
    }
}