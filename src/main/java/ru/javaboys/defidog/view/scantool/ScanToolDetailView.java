package ru.javaboys.defidog.view.scantool;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "scan-tools/:id", layout = MainView.class)
@ViewController(id = "ScanTool.detail")
@ViewDescriptor(path = "scan-tool-detail-view.xml")
@EditedEntityContainer("scanToolDc")
public class ScanToolDetailView extends StandardDetailView<ScanTool> {
}