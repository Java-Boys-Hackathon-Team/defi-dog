package ru.javaboys.defidog.view.audit;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.codeeditor.CodeEditor;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaboys.defidog.components.GraphPanel;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.ProtocolKind;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.repositories.ChangeSetRepository;
import ru.javaboys.defidog.repositories.CryptocurrencyRepository;
import ru.javaboys.defidog.repositories.DeFiProtocolRepository;
import ru.javaboys.defidog.view.main.MainView;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Route(value = "audit", layout = MainView.class)
@ViewController(id = "Audit")
@ViewDescriptor(path = "audit.xml")
@RequiredArgsConstructor
public class Audit extends StandardView {

    @ViewComponent
    private H3 protocolNameH3;
    @ViewComponent
    private Paragraph protocolDescriptionP;
    @Autowired
    private Notifications notifications;
    @ViewComponent
    private CodeEditor sourceCodeEditor;
    @ViewComponent
    private Div graphContainer;
    @ViewComponent
    private CollectionContainer<SourceCodeChangeSet> sourceCodeChangesDc;
    @ViewComponent
    private CollectionContainer<AbiChangeSet> abiChangesDc;
    @ViewComponent
    private Div statusDot;
    @ViewComponent
    private Span statusLabel;
    @ViewComponent
    private CodeEditor abiCodeEditor;

    @Autowired
    private DeFiProtocolRepository deFiProtocolRepository;
    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;
    @Autowired
    private ChangeSetRepository changeSetRepository;

    private UUID protocolId;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UUID protocolId = null;
        ProtocolKind protocolKind = null;

        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        Map<String, List<String>> paramsMap = queryParameters.getParameters();

        try {
            protocolId = Optional.ofNullable(paramsMap.get("id"))
                    .flatMap(list -> list.stream().findFirst())
                    .map(UUID::fromString)
                    .orElseThrow(() -> new IllegalArgumentException("Missing or invalid 'id'"));

            protocolKind = Optional.ofNullable(paramsMap.get("kind"))
                    .flatMap(list -> list.stream().findFirst())
                    .map(ProtocolKind::valueOf)
                    .orElseThrow(() -> new IllegalArgumentException("Missing or invalid 'kind'"));
        } catch (Exception e) {
            getUI().ifPresent(ui ->
                    ui.navigate("main")
            );
        }
        this.protocolId = protocolId;

        if (!validateInputs(protocolKind, protocolId)) {
            return;
        }

        List<SmartContract> contracts = getContracts(protocolKind, protocolId);
        renderProtocolTexts(protocolId);
        renderChanges(contracts);
        renderStatusDot();
    }

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {

    }

    @Subscribe
    public void onReady(ReadyEvent e) {
        renderDependencyGraph();
    }

    @Subscribe("copyCodeBtn")
    public void onCopyCodeClicked(ClickEvent<Button> event) {
        String code = sourceCodeEditor.getValue();
        if (code != null && !code.isBlank()) {
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("navigator.clipboard.writeText($0)", code);
                notifications.create("Исходный код скопирован ✅")
                        .withPosition(Notification.Position.TOP_END)
                        .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
                        .show();
            });
        } else {
            notifications.create("Исходный код пуст").withThemeVariant(NotificationVariant.LUMO_CONTRAST).show();
        }
    }

    @Subscribe("copyAbiBtn")
    public void onCopyAbiClicked(ClickEvent<Button> event) {
        String abi = abiCodeEditor.getValue();
        if (abi != null && !abi.isBlank()) {
            getUI().ifPresent(ui -> {
                ui.getPage().executeJs("navigator.clipboard.writeText($0)", abi);
                notifications.create("ABI скопирован ✅")
                        .withPosition(Notification.Position.TOP_END)
                        .withThemeVariant(NotificationVariant.LUMO_SUCCESS)
                        .show();
            });
        } else {
            notifications.create("ABI пуст").withThemeVariant(NotificationVariant.LUMO_CONTRAST).show();
        }
    }

    private void renderDependencyGraph() {
        Optional<String> graphJsonOpt = deFiProtocolRepository.findGraphJsonById(protocolId);

        GraphPanel graphPanel = new GraphPanel();

        graphJsonOpt.ifPresent(graphJson -> configureGraphPanel(graphPanel, graphJson));

        graphContainer.removeAll();
        graphContainer.add(graphPanel);
    }

    private void configureGraphPanel(GraphPanel graphPanel, String graphJson) {
        graphPanel.setGraphJson(graphJson);
        /*graphPanel.setOnNodeClick(contractId -> {
            // TODO: заменить на подгрузку из БД
            String code = "// Contract code for ID: " + contractId + "\nint a = 5;";
            sourceCodeEditor.setValue(code);
        });*/
    }

    private boolean validateInputs(ProtocolKind protocolKind, UUID protocolId) {
        if (protocolId == null || protocolKind == null) {
            notifications.create("Ошибка: не передан ID или тип объекта").show();
            return false;
        }
        return true;
    }

    private List<SmartContract> getContracts(ProtocolKind protocolKind, UUID protocolId) {
        return switch (protocolKind) {
            case DEFI -> deFiProtocolRepository.findContracts(protocolId);
            case CRYPTOCURRENCY -> cryptocurrencyRepository.findContracts(protocolId);
        };
    }

    private void renderProtocolTexts(UUID protocolId) {
        protocolNameH3.setText(
                deFiProtocolRepository.findNameById(protocolId).orElse("Не найдено")
        );
        protocolDescriptionP.setText(
                deFiProtocolRepository.findDescriptionById(protocolId).orElse("Описание отсутствует")
        );
    }

    private void renderStatusDot() {
        List<SourceCodeChangeSet> changes = sourceCodeChangesDc.getItems();

        String criticality = changes.isEmpty()
                ? "UNKNOWN"
                : Optional.ofNullable(changes.get(0).getAuditReport())
                .map(AuditReport::getCriticality)
                .orElse("UNKNOWN");

        // Очистка текущих классов и установка базового
        statusDot.getClassNames().clear();
        statusDot.addClassName("fancy-dot");

        // Добавление специфичного класса
        String statusClass = switch (criticality) {
            case "CRITICAL" -> "status-critical";
            case "HIGH"     -> "status-high";
            case "NORMAL"   -> "status-normal";
            default         -> "status-unknown";
        };
        statusDot.addClassName(statusClass);

        // Отображение текстовой подписи
        statusLabel.setText(
                switch (criticality) {
                    case "CRITICAL" -> "Critical";
                    case "HIGH"     -> "High";
                    case "NORMAL"   -> "Normal";
                    default         -> "Unknown";
                }
        );
    }

    private void renderChanges(List<SmartContract> contracts) {
        if (contracts == null || contracts.isEmpty()) {
            sourceCodeChangesDc.setItems(List.of());
            abiChangesDc.setItems(List.of());
            sourceCodeEditor.setValue("// Исходный код отсутствует");
            abiCodeEditor.setValue("// ABI отсутствует");
            return;
        }

        // Берем первый sourceCode из любого смарта
        SourceCode sourceCode = contracts.stream()
                .map(SmartContract::getSources)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (sourceCode == null) {
            sourceCodeChangesDc.setItems(List.of());
            abiChangesDc.setItems(List.of());
            sourceCodeEditor.setValue("// Исходный код отсутствует");
            abiCodeEditor.setValue("// ABI отсутствует");
            return;
        }

        UUID sourceId = sourceCode.getId();

        List<SourceCodeChangeSet> codeChanges = changeSetRepository.loadCodeChanges(sourceId);
        List<AbiChangeSet> abiChanges = changeSetRepository.loadAbiChanges(sourceId);

        sourceCodeChangesDc.setItems(codeChanges);
        abiChangesDc.setItems(abiChanges);

        // Установка значений в code editor
        sourceCodeEditor.setValue(
                sourceCode.getLastKnownSourceCode() != null ? sourceCode.getLastKnownSourceCode() : "// Исходный код отсутствует"
        );

        abiCodeEditor.setValue(
                sourceCode.getLastKnownAbi() != null ? sourceCode.getLastKnownAbi() : "// ABI отсутствует"
        );
    }
}
