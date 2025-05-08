package ru.javaboys.defidog.view.main;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.app.main.StandardMainView;
import io.jmix.flowui.component.combobox.JmixComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaboys.defidog.crypto.CryptocurrencyService;
import ru.javaboys.defidog.entity.BlockchainNetwork;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;

@Route("")
@ViewController(id = "MainView")
@ViewDescriptor(path = "main-view.xml")
public class MainView extends StandardMainView {

    @Autowired
    protected Notifications notifications;

    @Autowired
    private CryptocurrencyService cryptocurrencyService;

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

        cryptocurrencyGrid.addComponentColumn(entity -> {
            Button actionButton = new Button("Run Audit");
            actionButton.addClickListener(clickEvent -> goToAuditCrypto(entity));
            return actionButton;
        }).setHeader("Audit");

        dexGrid.addComponentColumn(entity -> {
            Button actionButton = new Button("Run Audit");
            actionButton.addClickListener(clickEvent -> goToAuditDex(entity));
            return actionButton;
        }).setHeader("Audit");
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
        //notifications.show("Update from CMC");
        cryptocurrencyService.updateCryptocurrenciesInfo();
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

}
