package ru.javaboys.defidog.view.audit;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
import ru.javaboys.defidog.audit.components.GraphPanel;
import ru.javaboys.defidog.entity.*;
import ru.javaboys.defidog.repositories.ChangeSetRepository;
import ru.javaboys.defidog.repositories.DeFiProtocolRepository;
import ru.javaboys.defidog.repositories.SourceCodeRepository;
import ru.javaboys.defidog.view.main.MainView;
import ru.javaboys.defidog.viewutils.ViewComponentsUtils;

import java.util.List;
import java.util.Map;
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
    @ViewComponent
    private HorizontalLayout header;

    @Autowired
    private DeFiProtocolRepository deFiProtocolRepository;
    @Autowired
    private ChangeSetRepository changeSetRepository;
    @Autowired
    private SourceCodeRepository sourceCodeRepository;

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

        if (!validateInputs(protocolId, protocolKind)) {
            return;
        }

        renderProtocolTexts(protocolId);
        renderChanges(protocolId, protocolKind);
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

    private boolean validateInputs(UUID protocolId, ProtocolKind protocolKind) {
        if (protocolId == null || protocolKind == null) {
            notifications.create("Ошибка: не передан ID или тип объекта").show();
            return false;
        }
        return true;
    }

    private void renderProtocolTexts(UUID protocolId) {
        protocolNameH3.setText(
                deFiProtocolRepository.findNameById(protocolId).orElse("Не найдено")
        );
        protocolDescriptionP.setText(
                deFiProtocolRepository.findDescriptionById(protocolId).orElse("Описание отсутствует")
        );
        DeFiProtocol protocol = deFiProtocolRepository.findById(protocolId);
        if (protocol != null) {
            Component logo = ViewComponentsUtils.createImageComponent(protocol, DeFiProtocol::getLogoImage);
            header.addComponentAsFirst(logo);
        }
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

    private void renderChanges(UUID protocolId, ProtocolKind kind) {
        Optional<SourceCode> sourceCodeOpt = sourceCodeRepository.findFirstSourceCodeByProtocolId(protocolId, kind);

        if (sourceCodeOpt.isEmpty()) {
            sourceCodeChangesDc.setItems(List.of());
            abiChangesDc.setItems(List.of());
            sourceCodeEditor.setValue("// Исходный код отсутствует");
            abiCodeEditor.setValue("// ABI отсутствует");
            return;
        }

        SourceCode sourceCode = sourceCodeOpt.get();
        UUID sourceId = sourceCode.getId();

        List<SourceCodeChangeSet> codeChanges = changeSetRepository.findCodeChanges(sourceId);
        List<AbiChangeSet> abiChanges = changeSetRepository.findAbiChanges(sourceId);

        sourceCodeChangesDc.setItems(codeChanges);
        abiChangesDc.setItems(abiChanges);

        sourceCodeEditor.setValue(
                Optional.ofNullable(sourceCode.getLastKnownSourceCode())
                        .orElse("// Исходный код отсутствует")
        );

        abiCodeEditor.setValue(
                Optional.ofNullable(sourceCode.getLastKnownAbi())
                        .orElse("// ABI отсутствует")
        );
    }
}
