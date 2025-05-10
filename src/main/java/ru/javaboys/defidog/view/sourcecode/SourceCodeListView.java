package ru.javaboys.defidog.view.sourcecode;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.Route;

import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.grid.DataGridColumn;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.DynamicListView;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "source-codes", layout = MainView.class)
@ViewController(id = "SourceCode.list")
@ViewDescriptor(path = "source-code-list-view.xml")
@LookupComponent("sourceCodesDataGrid")
@DialogMode(width = "64em")
public class SourceCodeListView extends StandardListView<SourceCode> implements DynamicListView {

    @ViewComponent
    private CollectionLoader<SourceCode> sourceCodesDl;

    @ViewComponent
    private DataGrid<SourceCode> sourceCodesDataGrid;

    @Subscribe
    public void onInit(InitEvent event) {
        customizeFetchedAt();
    }

    @Override
    public void activate() {
        sourceCodesDl.load();
    }

    private void customizeFetchedAt() {
        DataGridColumn<SourceCode> column = sourceCodesDataGrid.getColumnByKey("fetchedAt");
        if (column != null) {
            column.setRenderer(LitRenderer.<SourceCode>of("${item.fetchedAt}")
                    .withProperty("fetchedAt", item -> {
                        OffsetDateTime dateTime = item.getFetchedAt();
                        return dateTime != null
                                ? dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                                : "";
                    }));
        }
    }

}