package ru.javaboys.defidog.view.notificationsettings;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.NotificationSettings;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "notification-settings/:id", layout = MainView.class)
@ViewController(id = "NotificationSettings.detail")
@ViewDescriptor(path = "notification-settings-detail-view.xml")
@EditedEntityContainer("notificationSettingsDc")
public class NotificationSettingsDetailView extends StandardDetailView<NotificationSettings> {
}