package ru.javaboys.defidog.view.sourcecodechangeset;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.HtmlObject;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "sourceCodeChangeSets/:id", layout = MainView.class)
@ViewController(id = "SourceCodeChangeSet.detail")
@ViewDescriptor(path = "source-code-change-set-detail-view.xml")
@EditedEntityContainer("sourceCodeChangeSetDc")
public class SourceCodeChangeSetDetailView extends StandardDetailView<SourceCodeChangeSet> {

    @ViewComponent
    private Div markdownHtml;
    @ViewComponent
    private InstanceContainer<SourceCodeChangeSet> sourceCodeChangeSetDc;
    @ViewComponent
    private Div changeSummaryField;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        SourceCodeChangeSet changeSet = sourceCodeChangeSetDc.getItem();

        renderAuditMarkdown(changeSet);
        changeSummaryField.getElement().setProperty("innerHTML", changeSet.getChangeSummary());
    }

    private void renderAuditMarkdown(SourceCodeChangeSet changeSet) {


        if (changeSet == null || changeSet.getAuditReport() == null || changeSet.getAuditReport().getSummary() == null) {
            markdownHtml.getElement().setProperty("innerHTML", "<p><em>Описание аудита отсутствует</em></p>");
            return;
        }

        String markdown = changeSet.getAuditReport().getSummary();

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        markdownHtml.getElement().setProperty("innerHTML", html);
    }
}