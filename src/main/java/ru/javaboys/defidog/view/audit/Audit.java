package ru.javaboys.defidog.view.audit;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.codeeditor.CodeEditor;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaboys.defidog.components.GraphPanel;
import ru.javaboys.defidog.entity.*;
import ru.javaboys.defidog.repositories.ChangeSetRepository;
import ru.javaboys.defidog.repositories.CryptocurrencyRepository;
import ru.javaboys.defidog.repositories.DeFiProtocolRepository;
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
    private static final UUID MOCK_PROTOCOL_ID = UUID.fromString("4f6e2c97-8ad6-45fb-9c77-c6b86a678f04");
    private static final ProtocolKind PROTOCOL_KIND = ProtocolKind.DEFI;

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
    private DataGrid<SourceCodeChangeSet> sourceCodeHistoryGrid;

    @Autowired
    private DeFiProtocolRepository deFiProtocolRepository;
    @Autowired
    private CryptocurrencyRepository cryptocurrencyRepository;
    @Autowired
    private ChangeSetRepository changeSetRepository;
    @ViewComponent
    private Span statusLabel;
    @ViewComponent
    private CodeEditor abiCodeEditor;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        if (!validateInputs()) return;

        List<SmartContract> contracts = getContracts();
        renderProtocolTexts();
        renderChanges(contracts);
        renderStatusDot();
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
        Optional<String> graphJsonOpt = deFiProtocolRepository.findGraphJsonById(MOCK_PROTOCOL_ID);

        GraphPanel graphPanel = new GraphPanel();

        graphJsonOpt.ifPresent(graphJson -> configureGraphPanel(graphPanel, graphJson));

        graphContainer.removeAll();
        graphContainer.add(graphPanel);
    }

    private void configureGraphPanel(GraphPanel graphPanel, String graphJson) {
        graphPanel.setGraphJson(graphJson);
        graphPanel.setOnNodeClick(contractId -> {
            // TODO: заменить на подгрузку из БД
            String code = "// Contract code for ID: " + contractId + "\nint a = 5;";
            sourceCodeEditor.setValue(code);
        });
    }

    private boolean validateInputs() {
        if (MOCK_PROTOCOL_ID == null || PROTOCOL_KIND == null) {
            notifications.create("Ошибка: не передан ID или тип объекта").show();
            return false;
        }
        return true;
    }

    private List<SmartContract> getContracts() {
        return switch (PROTOCOL_KIND) {
            case DEFI -> deFiProtocolRepository.findContracts(MOCK_PROTOCOL_ID);
            case CRYPTOCURRENCY -> cryptocurrencyRepository.findContracts(MOCK_PROTOCOL_ID);
        };
    }

    private void renderProtocolTexts() {
        protocolNameH3.setText(
                deFiProtocolRepository.findNameById(MOCK_PROTOCOL_ID).orElse("Не найдено")
        );
        protocolDescriptionP.setText(
                deFiProtocolRepository.findDescriptionById(MOCK_PROTOCOL_ID).orElse("Описание отсутствует")
        );
    }

    private void renderStatusDot() {
        List<SourceCodeChangeSet> changes = sourceCodeChangesDc.getItems();

        String criticality = changes.isEmpty()
                ? "UNKNOWN"
                : Optional.ofNullable(changes.get(0).getAuditReport())
                .map(AuditReport::getCriticality)
                .map(Enum::name)
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
            return;
        }

        UUID sourceId = sourceCode.getId();

        List<SourceCodeChangeSet> codeChanges = changeSetRepository.loadCodeChanges(sourceId);
        List<AbiChangeSet> abiChanges = changeSetRepository.loadAbiChanges(sourceId);

        sourceCodeChangesDc.setItems(codeChanges);
        abiChangesDc.setItems(abiChanges);
    }
}
