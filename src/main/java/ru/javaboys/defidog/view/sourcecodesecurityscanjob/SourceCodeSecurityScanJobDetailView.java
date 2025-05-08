package ru.javaboys.defidog.view.sourcecodesecurityscanjob;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "source-code-security-scan-jobs/:id", layout = MainView.class)
@ViewController(id = "SourceCodeSecurityScanJob.detail")
@ViewDescriptor(path = "source-code-security-scan-job-detail-view.xml")
@EditedEntityContainer("sourceCodeSecurityScanJobDc")
public class SourceCodeSecurityScanJobDetailView extends StandardDetailView<SourceCodeSecurityScanJob> {
}