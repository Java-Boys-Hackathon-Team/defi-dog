package ru.javaboys.defidog.view.smartcontract;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "smart-contracts/:id", layout = MainView.class)
@ViewController(id = "SmartContract.detail")
@ViewDescriptor(path = "smart-contract-detail-view.xml")
@EditedEntityContainer("smartContractDc")
public class SmartContractDetailView extends StandardDetailView<SmartContract> {
}