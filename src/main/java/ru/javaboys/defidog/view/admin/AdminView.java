package ru.javaboys.defidog.view.admin;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Route;

import io.jmix.flowui.UiComponents;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.DynamicListView;
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

    @Subscribe
    public void onInit(InitEvent event) {
        tabSheet.addSelectedChangeListener(e -> {
            e.getSelectedTab().getId()
                    .ifPresent(this::loadTabContent);
        });

        loadTabContent("sources");
    }

    private void loadTabContent(String tabId) {
        contentBox.removeAll();

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
            ((DynamicListView) listView).activate();
            contentBox.add(listView);
        }
    }

}