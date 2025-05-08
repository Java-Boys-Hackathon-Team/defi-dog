package ru.javaboys.defidog.view.sourcecodesecurityscanjob;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;
import ru.javaboys.defidog.view.main.MainView;


@Route(value = "source-code-security-scan-jobs", layout = MainView.class)
@ViewController(id = "SourceCodeSecurityScanJob.list")
@ViewDescriptor(path = "source-code-security-scan-job-list-view.xml")
@LookupComponent("sourceCodeSecurityScanJobsDataGrid")
@DialogMode(width = "64em")
public class SourceCodeSecurityScanJobListView extends StandardListView<SourceCodeSecurityScanJob> {
}