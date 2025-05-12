package ru.javaboys.defidog.view.scantool;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.viewutils.DynamicListView;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "scan-tools", layout = MainView.class)
@ViewController(id = "ScanTool.list")
@ViewDescriptor(path = "scan-tool-list-view.xml")
@LookupComponent("scanToolsDataGrid")
@DialogMode(width = "64em")
public class ScanToolListView extends StandardListView<ScanTool> implements DynamicListView {

    @ViewComponent
    private CollectionLoader<ScanTool> scanToolsDl;

    @Override
    public void activate() {
        scanToolsDl.load();
    }
}