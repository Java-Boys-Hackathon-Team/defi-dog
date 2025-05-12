package ru.javaboys.defidog.view.main;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import io.jmix.core.Sort;
import io.jmix.flowui.model.CollectionContainer;
import jakarta.inject.Inject;
import org.apache.hc.core5.annotation.Contract;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import io.jmix.datatoolsflowui.view.entityinspector.EntityInspectorListView;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.tabsheet.JmixTabSheet;
import io.jmix.flowui.facet.Timer;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.theme.ThemeUtils;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.crypto.CryptocurrencyService;
import ru.javaboys.defidog.entity.*;

import ru.javaboys.defidog.view.admin.AdminView;
import ru.javaboys.defidog.view.settings.SettingsView;
import ru.javaboys.defidog.viewutils.ViewComponentsUtils;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {

    @Autowired
    protected Notifications notifications;

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @Autowired
    private ViewNavigators viewNavigators;

    @ViewComponent("cryptocurrencyLoader")
    private CollectionLoader<Cryptocurrency> cryptocurrencyLoader;

    @ViewComponent
    private JmixComboBox<BlockchainNetwork> blockchainNetworkComboBox;

    @ViewComponent
    private DataGrid<Cryptocurrency> cryptocurrencyGrid;

    @ViewComponent
    private DataGrid<DeFiProtocol> dexGrid;

    @ViewComponent
    private VerticalLayout logoLayout;

    @ViewComponent
    private Button homeButton;

    @ViewComponent
    private CollectionContainer<Cryptocurrency> cryptocurrencyDg;

    @ViewComponent
    private CollectionContainer<DeFiProtocol> dexDg;

    @Subscribe
    public void onInit(InitEvent event) {
        blockchainNetworkComboBox.setValue(BlockchainNetwork.ETHEREUM);
        blockchainNetworkComboBox.setItemLabelGenerator(BlockchainNetwork::toString);
        setVisiblesGrid(true, false);

        setColumnsDataGrids();
        cryptocurrencyGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        dexGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        Objects.requireNonNull(cryptocurrencyDg.getSorter()).sort(Sort.by(Sort.Order.desc("marketCap")));
        Objects.requireNonNull(dexDg.getSorter()).sort(Sort.by(Sort.Order.asc("cmcId")));

        Image logo = new Image("icons/lader.png", "DeFi App Logo");
        logo.setWidth("400px");
        logoLayout.add(logo);

        homeButton.addClassName("home-button-icon");
    }

    private void setColumnsDataGrids() {

        cryptocurrencyGrid.addColumn(
                new ComponentRenderer<>(item ->
                        ViewComponentsUtils.createImageComponent(item, Cryptocurrency::getLogoImage)))
                .setHeader("Logo")
                .setKey("logoImageColumn");

        cryptocurrencyGrid.addComponentColumn(crypto -> {
                    Span span = new Span(crypto.getName());
                    span.getStyle().set("font-weight", "bold");
                    return span;
                })
                .setTextAlign(ColumnTextAlign.START)
                .setHeader("Name");

        cryptocurrencyGrid.addComponentColumn(crypto -> new Span(crypto.getTicker()))
                .setTextAlign(ColumnTextAlign.START)
                .setHeader("Ticker");

        cryptocurrencyGrid.addComponentColumn(crypto -> {
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setGroupingSeparator(',');
                    symbols.setDecimalSeparator('.');

                    DecimalFormat format = new DecimalFormat("#,##0.00", symbols);

                    BigDecimal price = crypto.getPrice();
                    String formatted = price != null ? "$" + format.format(price) : "$—";

                    return new Span(formatted);
                })
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Price");

        cryptocurrencyGrid.addComponentColumn(crypto -> {
                    BigDecimal percentChange24h = crypto.getPercentChange24h();

                    String textSpan;
                    if (percentChange24h == null) {
                        textSpan = "-";
                    } else {
                        int cmp = percentChange24h.compareTo(BigDecimal.ZERO);
                        if (cmp < 0) textSpan = "▼ " + percentChange24h.abs() + "%";
                        else if (cmp > 0) textSpan = "▲ " + percentChange24h + "%";
                        else textSpan = percentChange24h + "%";
                    }
                    Span span = new Span(textSpan);

                    if (percentChange24h != null) {
                        int cmp = percentChange24h.compareTo(BigDecimal.ZERO);
                        if (cmp > 0) {
                            span.getStyle().set("color", "#16C784");
                        } else if (cmp < 0) {
                            span.getStyle().set("color", "#EA3943");
                        }
                    }
                    span.getStyle().set("font-weight", "bold");

                    return span;
                })
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("24h %");

        cryptocurrencyGrid.addComponentColumn(crypto -> {
                    Span span = new Span();
                    BigDecimal marketCap = crypto.getMarketCap();

                    if (marketCap != null) {
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setGroupingSeparator(',');
                        DecimalFormat format = new DecimalFormat("#,##0", symbols);

                        span.setText("$" + format.format(marketCap));
                    } else {
                        span.setText("—");
                    }

                    return span;
                })
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Market Cap");

        cryptocurrencyGrid.addComponentColumn(entity -> {
            Button actionButton = new Button();
            Icon icon = new Icon(VaadinIcon.PLAY);
            icon.setColor("white");
            actionButton.setIcon(icon);
            actionButton.setTooltipText("Перейти к аудиту");
            if (contractsHaveAudit(entity.getContracts())) {
                actionButton.addClickListener(clickEvent
                        -> goToAudit(entity.getId(), ProtocolKind.CRYPTOCURRENCY));
                actionButton.addClassName("audit-background-available");
            } else {
                actionButton.addClickListener(clickEvent
                        -> notifications.show("Для этого актива пока нет Аудита"));
                actionButton.addClassName("audit-background-notavailable");
            }
            return actionButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setHeader("Audit");

        dexGrid.addColumn(
                new ComponentRenderer<>(item ->
                        ViewComponentsUtils.createImageComponent(item, DeFiProtocol::getLogoImage)))
                .setHeader("Logo")
                .setKey("logoImageColumn");

        dexGrid.addComponentColumn(dex -> {
                    Span span = new Span(dex.getName());
                    span.getStyle().set("font-weight", "bold");
                    return span;
                })
                .setTextAlign(ColumnTextAlign.START)
                .setHeader("Name");

        dexGrid.addComponentColumn(dex -> {
                    String url = dex.getOfficialSite();
                    Anchor anchor = new Anchor(url, url);
                    anchor.setTarget("_blank"); // Открыть в новой вкладке
                    return anchor;
                })
                .setTextAlign(ColumnTextAlign.START)
                .setHeader("Official Site");

        dexGrid.addComponentColumn(entity -> {
            Button actionButton = new Button();
            Icon icon = new Icon(VaadinIcon.PLAY);
            icon.setColor("white");
            actionButton.setIcon(icon);
            actionButton.setTooltipText("Перейти к аудиту");
            if (contractsHaveAudit(entity.getContracts())) {
                actionButton.addClickListener(clickEvent
                        -> goToAudit(entity.getId(), ProtocolKind.DEFI));
                actionButton.addClassName("audit-background-available");
            } else {
                actionButton.addClickListener(clickEvent
                        -> notifications.show("Для этого актива пока нет Аудита"));
                actionButton.addClassName("audit-background-notavailable");
            }
            return actionButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setHeader("Audit");

    }

    @Subscribe("timer")
    public void onTimerTimerAction(final Timer.TimerActionEvent event) {
        updateCryptocurrencyGrid();
    }

    @Subscribe("tabs")
    public void onCryptoTableTab(final JmixTabSheet.SelectedChangeEvent event) {
        String tabId = event.getSelectedTab().getId().orElse(null);
        switch (tabId) {
            case "cryptoTableTab" -> setVisiblesGrid(true, false);
            case "dexTableTab" -> setVisiblesGrid(false, true);
        }
    }

    @Subscribe(id = "homeButton", subject = "clickListener")
    public void onHomeButtonClick(final ClickEvent<JmixButton> event) {
        viewNavigators.view(this, MainView.class).navigate();
    }

    @Subscribe(id = "adminButton", subject = "clickListener")
    public void onAdminButtonClick(final ClickEvent<JmixButton> event) {
        viewNavigators.view(this, AdminView.class).navigate();
    }

    @Subscribe(id = "inspectorButton", subject = "clickListener")
    public void onInspectorButtonClick(final ClickEvent<JmixButton> event) {
        viewNavigators.view(this, EntityInspectorListView.class).navigate();
    }

    @Subscribe(id = "settingsButton", subject = "clickListener")
    public void onSettingsButtonClick(final ClickEvent<JmixButton> event) {
        viewNavigators.view(this, SettingsView.class).navigate();
    }

    private void setVisiblesGrid(Boolean visibleCryptocurrencyGrid, Boolean visibleDexGrid) {
        cryptocurrencyGrid.setVisible(visibleCryptocurrencyGrid);
        dexGrid.setVisible(visibleDexGrid);
    }

    private void goToAudit(UUID id, ProtocolKind kind) {
        if (id == null || kind == null) return;

        getUI().ifPresent(ui -> ui.navigate("audit",
                QueryParameters.simple(
                        Map.of(
                                "id", id.toString(),
                                "kind", kind.name()
                        )
                )
        ));
    }

    private boolean contractsHaveAudit(List<SmartContract> contracts) {
        boolean haveAudit = false;
        for (SmartContract contract : contracts) {
            if (!contract.getAuditReports().isEmpty()) {
                haveAudit = true;
                break;
            }
        }
        return haveAudit;
    }

    private void updateCryptocurrencyGrid() {
        cryptocurrencyLoader.load();
    }

    @Subscribe("themeSwitcher.systemThemeItem.systemThemeAction")
    public void onThemeSwitcherSystemThemeItemSystemThemeAction(final ActionPerformedEvent event) {
        ThemeUtils.applySystemTheme();
    }

    @Subscribe("themeSwitcher.lightThemeItem.lightThemeAction")
    public void onThemeSwitcherLightThemeItemLightThemeAction(final ActionPerformedEvent event) {
        ThemeUtils.applyLightTheme();
    }

    @Subscribe("themeSwitcher.darkThemeItem.darkThemeAction")
    public void onThemeSwitcherDarkThemeItemDarkThemeAction(final ActionPerformedEvent event) {
        ThemeUtils.applyDarkTheme();
    }
}
