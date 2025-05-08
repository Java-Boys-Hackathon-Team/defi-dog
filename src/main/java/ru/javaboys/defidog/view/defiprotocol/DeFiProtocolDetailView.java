package ru.javaboys.defidog.view.defiprotocol;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "de-fi-protocols/:id", layout = MainView.class)
@ViewController(id = "DeFiProtocol.detail")
@ViewDescriptor(path = "de-fi-protocol-detail-view.xml")
@EditedEntityContainer("deFiProtocolDc")
public class DeFiProtocolDetailView extends StandardDetailView<DeFiProtocol> {
}