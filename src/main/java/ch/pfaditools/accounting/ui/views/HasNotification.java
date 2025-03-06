package ch.pfaditools.accounting.ui.views;


import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public interface HasNotification {

    int DEFAULT_SHOW_TIME = 5_000;

    default void showInfoNotification(String message) {
        Notification.show(message, DEFAULT_SHOW_TIME, Notification.Position.BOTTOM_START);
    }

    default void showWarningNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

    default void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    default void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
