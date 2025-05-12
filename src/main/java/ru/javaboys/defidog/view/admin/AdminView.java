package ru.javaboys.defidog.view.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import io.jmix.flowui.UiComponents;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.viewutils.DynamicListView;
import ru.javaboys.defidog.view.cryptocurrency.CryptocurrencyListView;
import ru.javaboys.defidog.view.defiprotocol.DeFiProtocolListView;
import ru.javaboys.defidog.view.main.MainView;
import ru.javaboys.defidog.view.scantool.ScanToolListView;
import ru.javaboys.defidog.view.smartcontract.SmartContractListView;
import ru.javaboys.defidog.view.sourcecode.SourceCodeListView;
import ru.javaboys.defidog.view.sourcecodesecurityscanjob.SourceCodeSecurityScanJobListView;

@Route(value = "admin-view", layout = MainView.class)
@ViewController(id = "AdminView")
@ViewDescriptor(path = "admin-view.xml")
public class AdminView extends StandardView {

    @ViewComponent
    private Tabs tabSheet;

    @ViewComponent
    private VerticalLayout contentBox;

    @Autowired
    private UiComponents uiComponents;

    private Map<String, Tab> tabs = new HashMap<>();

    @Subscribe
    public void onInit(InitEvent event) {
        tabSheet.addSelectedChangeListener(e -> {
            if (e.getSelectedTab() != null) {
                e.getSelectedTab().getId()
                        .ifPresent(this::loadTabContent);
            }
        });

        for (int i = 0; i < tabSheet.getTabCount(); i++) {
            Tab tab = tabSheet.getTabAt(i);
            if (tab.getId().isPresent()) {
                tabs.put(tab.getId().get(), tab);
            }
        }

        String tabId = Optional.ofNullable(VaadinSession.getCurrent().getAttribute("lastKnownTabId"))
                .orElse("sources")
                .toString();
        tabSheet.setSelectedTab(tabs.get(tabId));
        loadTabContent(tabId);
    }

    private void loadTabContent(String tabId) {
        contentBox.removeAll();

        VaadinSession.getCurrent().setAttribute("lastKnownTabId", tabId);
        Class<? extends StandardListView<?>> listViewClass = switch (tabId) {
            case "sources" -> SourceCodeListView.class;
            case "tools" -> ScanToolListView.class;
            case "tasks" -> SourceCodeSecurityScanJobListView.class;
            case "contracts" -> SmartContractListView.class;
            case "currencies" -> CryptocurrencyListView.class;
            case "protocols" -> DeFiProtocolListView.class;
            default -> null;
        };

        if (listViewClass != null) {
            StandardListView<?> listView = uiComponents.create(listViewClass);
            if (listView instanceof DynamicListView) {
                ((DynamicListView) listView).activate();
            }
            contentBox.add(listView);
        }
    }

}