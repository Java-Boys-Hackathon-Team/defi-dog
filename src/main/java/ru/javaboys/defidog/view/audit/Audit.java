package ru.javaboys.defidog.view.audit;


import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.HtmlObject;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.ui.LoadMode;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import io.jmix.flowui.component.codeeditor.CodeEditor;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.View;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaboys.defidog.components.GraphPanel;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.view.main.MainView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Route(value = "audit", layout = MainView.class)
@ViewController(id = "Audit")
@ViewDescriptor(path = "audit.xml")
@RequiredArgsConstructor
public class Audit extends StandardView {
    // todo: после реализации главной страницы, это мы уберем
    private static final UUID MOCK_PROTOCOL_ID = UUID.fromString("4f6e2c97-8ad6-45fb-9c77-c6b86a678f04");

    private static final String FIND_PROTOCOL_NAME_BY_ID = "select p.name from DeFiProtocol p where p.id = :protocolId";
    private static final String FIND_PROTOCOL_DESCRIPTION_BY_ID = "select p.description from DeFiProtocol p where p.id = :protocolId";
    private static final String FIND_DEPENDENCY_GRAPH_BY_ID = "select c.graphJson from ContractDependenciesGraph c where c.deFiProtocol.id = :protocolId";


    @ViewComponent
    private H3 protocolNameH3;
    @ViewComponent
    private Paragraph protocolDescriptionP;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private FetchPlans fetchPlans;
    @ViewComponent
    private CodeEditor sourceCodeEditor;
    @ViewComponent
    private Div graphContainer;
    @ViewComponent
    private CollectionContainer<SourceCodeChangeSet> sourceCodeChangesDc;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        Optional<String> protocolName = dataManager.loadValue(
                        FIND_PROTOCOL_NAME_BY_ID, String.class)
                .parameter("protocolId", MOCK_PROTOCOL_ID)
                .optional();

        Optional<String> protocolDescription = dataManager.loadValue(
                        FIND_PROTOCOL_DESCRIPTION_BY_ID, String.class)
                .parameter("protocolId", MOCK_PROTOCOL_ID)
                .optional();

        FetchPlan fetchPlan = fetchPlans.builder(SourceCodeChangeSet.class)
                .add("commitHash")
                .add("changeSummary")
                .add("createdDate")
                .add("sourceCode", builder -> builder
                        .add("sourceType")
                        .add("repoUrl")
                        .add("branch")
                )
                .add("auditReport", builder -> builder
                        .add("criticality")
                )
                .build();

        List<SourceCodeChangeSet> result = dataManager.load(SourceCodeChangeSet.class)
                .query("""
                select cs from SmartContract sc
                join sc.sources s
                join SourceCodeChangeSet cs on cs.sourceCode = s
                where sc.protocol.id = :protocolId
                order by cs.createdDate desc
            """)
                .parameter("protocolId", MOCK_PROTOCOL_ID)
                .fetchPlan(fetchPlan)
                .list();

        sourceCodeChangesDc.setItems(result);

        protocolNameH3.setText(protocolName.orElseThrow(() -> new IllegalStateException("Protocol name not found")));
        protocolDescriptionP.setText(protocolDescription.orElseThrow(() -> new IllegalStateException("Protocol description not found")));
    }

    @Subscribe
    public void onReady(ReadyEvent e) {
        Optional<String> graphJsonOpt = dataManager.loadValue(
                        FIND_DEPENDENCY_GRAPH_BY_ID, String.class)
                .parameter("protocolId", MOCK_PROTOCOL_ID)
                .optional();

        // Создаём панель графа
        GraphPanel graphPanel = new GraphPanel();

        // Если граф найден — отрисовываем
        graphJsonOpt.ifPresent(graphJson -> {
            graphPanel.setGraphJson(graphJson);

            graphPanel.setOnNodeClick(contractId -> {
                // заглушка — позже подгрузим из БД
                String code = "// Contract code for ID: " + contractId + "\nint a = 5;";
                sourceCodeEditor.setValue(code);
            });
        });

        // Вставляем граф в layout, если ты это ещё не сделал
        graphContainer.removeAll();
        graphContainer.add(graphPanel);
    }
}