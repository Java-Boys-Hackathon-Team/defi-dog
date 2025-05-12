package ru.javaboys.defidog.view.defiprotocol;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.viewutils.DynamicListView;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "de-fi-protocols", layout = MainView.class)
@ViewController(id = "DeFiProtocol.list")
@ViewDescriptor(path = "de-fi-protocol-list-view.xml")
@LookupComponent("deFiProtocolsDataGrid")
@DialogMode(width = "64em")
public class DeFiProtocolListView extends StandardListView<DeFiProtocol> implements DynamicListView {

    @ViewComponent
    private CollectionLoader<ScanTool> deFiProtocolsDl;

    @Override
    public void activate() {
        deFiProtocolsDl.load();
    }

}