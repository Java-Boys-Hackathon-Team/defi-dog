package ru.javaboys.defidog.view.settings;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

import io.jmix.flowui.UiComponents;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.view.main.MainView;
import ru.javaboys.defidog.view.notificationsettings.NotificationUserSettingsDetailView;

@Route(value = "settings-view", layout = MainView.class)
@ViewController(id = "SettingsView")
@ViewDescriptor(path = "settings-view.xml")
public class SettingsView extends StandardView {

    @ViewComponent
    private Tabs tabSheet;

    @ViewComponent
    private VerticalLayout contentBox;

    @Autowired
    private UiComponents uiComponents;

    @Subscribe
    public void onInit(InitEvent event) {
        tabSheet.addSelectedChangeListener(e -> {
            e.getSelectedTab().getId()
                    .ifPresent(this::loadTabContent);
        });

        loadTabContent("notifications");
    }

    private void loadTabContent(String tabId) {
        contentBox.removeAll();

        Class<? extends StandardDetailView<?>> listViewClass = switch (tabId) {
            case "notifications" -> NotificationUserSettingsDetailView.class;
            default -> null;
        };

        if (listViewClass != null) {
            StandardDetailView<?> listView = uiComponents.create(listViewClass);
            contentBox.add(listView);
        }
    }

}