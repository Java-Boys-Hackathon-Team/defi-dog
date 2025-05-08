package ru.javaboys.defidog.view.sourcecode;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "source-codes/:id", layout = MainView.class)
@ViewController(id = "SourceCode.detail")
@ViewDescriptor(path = "source-code-detail-view.xml")
@EditedEntityContainer("sourceCodeDc")
public class SourceCodeDetailView extends StandardDetailView<SourceCode> {
}