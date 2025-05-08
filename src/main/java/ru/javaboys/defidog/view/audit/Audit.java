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
import io.jmix.flowui.Notifications;
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
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.entity.ProtocolKind;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.view.main.MainView;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Route(value = "audit", layout = MainView.class)
@ViewController(id = "Audit")
@ViewDescriptor(path = "audit.xml")
@RequiredArgsConstructor
public class Audit extends StandardView {
    // todo: после реализации главной страницы, это мы уберем
    private static final UUID MOCK_PROTOCOL_ID = UUID.fromString("4f6e2c97-8ad6-45fb-9c77-c6b86a678f04");
    private static final ProtocolKind PROTOCOL_KIND = ProtocolKind.DEFI;

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
    @Autowired
    private Notifications notifications;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (MOCK_PROTOCOL_ID == null || PROTOCOL_KIND == null) {
            notifications.create("Ошибка: не передан ID или тип объекта").show();
            return;
        }

        List<SmartContract> contracts = switch (PROTOCOL_KIND) {
            case DEFI -> {
                DeFiProtocol protocol = dataManager.load(DeFiProtocol.class)
                        .id(MOCK_PROTOCOL_ID)
                        .fetchPlan("deFiProtocol.smartContracts")
                        .optional().orElse(null);
                yield protocol != null ? protocol.getContracts() : List.of();
            }
            case CRYPTOCURRENCY -> {
                Cryptocurrency crypto = dataManager.load(Cryptocurrency.class)
                        .id(MOCK_PROTOCOL_ID)
                        .fetchPlan("cryptocurrency.smartContracts")
                        .optional().orElse(null);
                yield crypto != null ? crypto.getContracts() : List.of();
            }
        };

        Optional<String> protocolName = dataManager.loadValue(
                        FIND_PROTOCOL_NAME_BY_ID, String.class)
                .parameter("protocolId", MOCK_PROTOCOL_ID)
                .optional();

        Optional<String> protocolDescription = dataManager.loadValue(
                        FIND_PROTOCOL_DESCRIPTION_BY_ID, String.class)
                .parameter("protocolId", MOCK_PROTOCOL_ID)
                .optional();

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

    /*private void loadChangesForContracts(List<SmartContract> contracts) {
        if (contracts == null || contracts.isEmpty()) {
            sourceCodeChangesDc.setItems(List.of());
            abiChangesDc.setItems(List.of());
            return;
        }

        List<UUID> sourceIds = contracts.stream()
                .map(SmartContract::getSources)
                .filter(Objects::nonNull)
                .map(SourceCode::getId)
                .toList();

        // Source code changes
        List<SourceCodeChangeSet> changes = dataManager.load(SourceCodeChangeSet.class)
                .query("select s from SourceCodeChangeSet s where s.sourceCode.id in :ids order by s.createdDate desc")
                .parameter("ids", sourceIds)
                .fetchPlan("sourceCodeChange-full") // или "_base", если нужно
                .list();

        sourceCodeChangesDc.setItems(changes);

        // ABI changes
        List<AbiChangeSet> abiChanges = dataManager.load(AbiChangeSet.class)
                .query("select a from AbiChangeSet a where a.sourceCode.id in :ids order by a.createdDate desc")
                .parameter("ids", sourceIds)
                .fetchPlan("_base")
                .list();

        abiChangesDc.setItems(abiChanges);
    }*/
}