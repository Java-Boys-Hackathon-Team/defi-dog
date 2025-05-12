package ru.javaboys.defidog.view.smartcontract;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.viewutils.DynamicListView;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "smart-contracts", layout = MainView.class)
@ViewController(id = "SmartContract.list")
@ViewDescriptor(path = "smart-contract-list-view.xml")
@LookupComponent("smartContractsDataGrid")
@DialogMode(width = "64em")
public class SmartContractListView extends StandardListView<SmartContract> implements DynamicListView {

    @ViewComponent
    private CollectionLoader<SmartContract> smartContractsDl;

    @Override
    public void activate() {
        smartContractsDl.load();
    }

}