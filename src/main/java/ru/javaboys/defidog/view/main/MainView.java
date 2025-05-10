package ru.javaboys.defidog.view.main;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.facet.Timer;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.theme.ThemeUtils;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaboys.defidog.crypto.CryptocurrencyService;
import ru.javaboys.defidog.entity.BlockchainNetwork;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {

    @Autowired
    protected Notifications notifications;

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @ViewComponent("cryptocurrencyLoader")
    private CollectionLoader<Cryptocurrency> cryptocurrencyLoader;

    @ViewComponent
    private JmixComboBox<BlockchainNetwork> blockchainNetworkComboBox;

    @ViewComponent
    private DataGrid<Cryptocurrency> cryptocurrencyGrid;

    @ViewComponent
    private DataGrid<DeFiProtocol> dexGrid;

    @ViewComponent
    private Button cryptocurrenciesButton;

    @ViewComponent
    private Button dexButton;

    @Subscribe
    public void onInit(InitEvent event) {
        blockchainNetworkComboBox.setValue(BlockchainNetwork.ETHEREUM);
        setVisiblesGrid(true, false);

        setColumnsDataGrids();
        cryptocurrencyGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        dexGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
    }

    private void setColumnsDataGrids() {

        cryptocurrencyGrid.addComponentColumn(crypto -> {
                    Span span = new Span(crypto.getName());
                    span.getStyle().set("font-weight", "bold");
                    return span;})
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

                    return new Span(formatted);})
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Price");

        cryptocurrencyGrid.addComponentColumn(crypto -> {
                    BigDecimal percentChange24h = crypto.getPercentChange24h();

                    String textSpan;
                    if (percentChange24h == null) {
                        textSpan = "-";
                    } else {
                        int cmp = percentChange24h.compareTo(BigDecimal.ZERO);
                        if (cmp < 0 ) textSpan = "▼ " + percentChange24h.abs() + "%";
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

                    return span;})
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

                    return span;})
                .setTextAlign(ColumnTextAlign.END)
                .setHeader("Market Cap");

        cryptocurrencyGrid.addComponentColumn(entity -> {
            Button actionButton = new Button("Run Audit");
            actionButton.addClickListener(clickEvent -> goToAuditCrypto(entity));
            return actionButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setHeader("Audit");

        dexGrid.addComponentColumn(dex -> {
                    Span span = new Span(dex.getName());
                    span.getStyle().set("font-weight", "bold");
                    return span;})
                .setTextAlign(ColumnTextAlign.START)
                .setHeader("Name");

        dexGrid.addComponentColumn(dex -> {
                    String url = dex.getOfficialSite();
                    Anchor anchor = new Anchor(url, url);
                    anchor.setTarget("_blank"); // Открыть в новой вкладке
                    return anchor;})
                .setTextAlign(ColumnTextAlign.START)
                .setHeader("Official Site");

        dexGrid.addComponentColumn(entity -> {
            Button actionButton = new Button("Run Audit");
            actionButton.addClickListener(clickEvent -> goToAuditDex(entity));
            return actionButton;
        }).setTextAlign(ColumnTextAlign.CENTER).setHeader("Audit");

    }

    @Subscribe("timer")
    public void onTimerTimerAction(final Timer.TimerActionEvent event) {
        updateCryptocurrencyGrid();
    }

    @Subscribe("cryptocurrenciesButton")
    public void onCryptocurrenciesButtonClick(ClickEvent<Button> event) {
        setVisiblesGrid(true, false);
    }

    @Subscribe("dexButton")
    public void onDexButtonClick(ClickEvent<Button> event) {
        setVisiblesGrid(false, true);
    }

    @Subscribe("updateButton")
    protected void onHelloButtonClick(ClickEvent<Button> event) {
        cryptocurrencyService.updateCryptocurrenciesInfo();
        updateCryptocurrencyGrid();
    }

    private void setVisiblesGrid(Boolean visibleCryptocurrencyGrid, Boolean visibleDexGrid) {
        cryptocurrencyGrid.setVisible(visibleCryptocurrencyGrid);
        dexGrid.setVisible(visibleDexGrid);
        cryptocurrenciesButton.setThemeName(visibleCryptocurrencyGrid ? "primary contrast" : "tertiary contrast");
        dexButton.setThemeName(visibleDexGrid ? "primary contrast" : "tertiary contrast");
    }

    private void goToAuditCrypto(Cryptocurrency cryptocurrency) {
        notifications.create("Button clicked for: " + cryptocurrency.getName()).show();
    }

    private void goToAuditDex(DeFiProtocol deFiProtocol) {
        notifications.create("Button clicked for: " + deFiProtocol.getName()).show();
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
