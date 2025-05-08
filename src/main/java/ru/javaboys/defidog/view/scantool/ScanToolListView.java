package ru.javaboys.defidog.view.scantool;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.view.main.MainView;


@Route(value = "scan-tools", layout = MainView.class)
@ViewController(id = "ScanTool.list")
@ViewDescriptor(path = "scan-tool-list-view.xml")
@LookupComponent("scanToolsDataGrid")
@DialogMode(width = "64em")
public class ScanToolListView extends StandardListView<ScanTool> {
}