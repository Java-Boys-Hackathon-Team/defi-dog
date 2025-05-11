package ru.javaboys.defidog.view.notificationsettings;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.DynamicListView;
import ru.javaboys.defidog.entity.NotificationSettings;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "notification-settingses", layout = MainView.class)
@ViewController(id = "NotificationSettings.list")
@ViewDescriptor(path = "notification-settings-list-view.xml")
@LookupComponent("notificationSettingsesDataGrid")
@DialogMode(width = "64em")
public class NotificationSettingsListView extends StandardListView<NotificationSettings> implements DynamicListView {

    @ViewComponent
    private CollectionLoader<NotificationSettings> notificationSettingsDl;

    @Override
    public void activate() {
        notificationSettingsDl.load();
    }

}