package ru.javaboys.defidog.view.abichangeset;

import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "abiChangeSets/:id", layout = MainView.class)
@ViewController(id = "AbiChangeSet.detail")
@ViewDescriptor(path = "abi-change-set-detail-view.xml")
@EditedEntityContainer("abiChangeSetDc")
public class AbiChangeSetDetailView extends StandardDetailView<AbiChangeSet> {
}