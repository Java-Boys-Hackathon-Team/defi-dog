package ru.javaboys.defidog.view.notificationsettings;

import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import io.jmix.core.DataManager;
import io.jmix.core.querycondition.PropertyCondition;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.DialogWindow;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.StandardOutcome;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.Supply;
import io.jmix.flowui.view.Target;
import io.jmix.flowui.view.View;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.entity.NotificationSettings;
import ru.javaboys.defidog.entity.User;
import ru.javaboys.defidog.view.cryptocurrency.CryptocurrencyListView;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "notification-settings", layout = MainView.class)
@ViewController(id = "NotificationUserSettings.detail")
@ViewDescriptor(path = "notification-user-settings-detail-view.xml")
@EditedEntityContainer("notificationSettingsDc")
public class NotificationUserSettingsDetailView extends StandardDetailView<NotificationSettings> {

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private HorizontalLayout currencyHeader;

    @ViewComponent
    private HorizontalLayout protocolHeader;

    @ViewComponent
    private NativeLabel currencyHeaderLabel;

    @ViewComponent
    private NativeLabel protocolHeaderLabel;

    @ViewComponent("currenciesListEmptyLabel")
    private NativeLabel currenciesListEmptyLabel;

    @ViewComponent("currenciesList")
    private VirtualList<Cryptocurrency> currenciesList;

    @ViewComponent("protocolsListEmptyLabel")
    private NativeLabel protocolsListEmptyLabel;

    @ViewComponent("protocolsList")
    private VirtualList<Cryptocurrency> protocolsList;

    @ViewComponent
    private CollectionContainer<Cryptocurrency> cryptocurrenciesDc;

    @ViewComponent
    private CollectionContainer<DeFiProtocol> protocolsDc;

    @ViewComponent
    private HorizontalLayout detailActions;

    private List<Cryptocurrency> initCryptocurrencies;
    private List<DeFiProtocol> initProtocols;

    @Subscribe
    public void onInit(InitEvent event) {
        currencyHeader.expand(currencyHeaderLabel);
        protocolHeader.expand(protocolHeaderLabel);
    }

    @Override
    protected void findEntityId(BeforeEnterEvent event) {
        String username = currentAuthentication.getUser().getUsername();
        User user = dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .one();

        NotificationSettings entity = dataManager.load(NotificationSettings.class)
                .condition(PropertyCondition.create("user", PropertyCondition.Operation.EQUAL, user))
                .optional()
                .orElseGet(() -> {
                    NotificationSettings settings = dataManager.create(NotificationSettings.class);
                    settings.setUser(user);
                    return settings;
                });
        super.setupEntityToEdit(entity);

        initCryptocurrencies = ListUtils.emptyIfNull(entity.getSubscribedCryptocurrencies());
        initProtocols = ListUtils.emptyIfNull(entity.getSubscribedDeFiProtocols());
    }

    /*
     * Currencies
     */

    @Supply(to = "currenciesList", subject = "renderer")
    private Renderer<Cryptocurrency> currenciesListRenderer() {
        return new ComponentRenderer<>(this::createCurrencyRenderer);
    }

    @Subscribe(id = "cryptocurrenciesDc", target = Target.DATA_CONTAINER)
    public void onCryptoCurrenciesChange(CollectionContainer.CollectionChangeEvent<Cryptocurrency> event) {
        boolean isEmpty = event.getSource().getItems().isEmpty();
        currenciesListEmptyLabel.setVisible(isEmpty);
        currenciesList.setVisible(!isEmpty);
    }

    @Subscribe(id = "currencyAddBtn", subject = "clickListener")
    public void onCurrencyAddAction(final ClickEvent<JmixButton> event) {
        dialogWindows.lookup(this, Cryptocurrency.class)
                .withLookupComponentMultiSelect(true)
                .withSelectHandler(cryptocurrencies -> {
                    cryptocurrenciesDc.getMutableItems().addAll(cryptocurrencies);
                    settingsChanged();
                }).open();
    }

    private Component createCurrencyRenderer(Cryptocurrency currency) {
        HorizontalLayout cardLayout = uiComponents.create(HorizontalLayout.class);
        cardLayout.setMargin(false);
        cardLayout.addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.BorderRadius.MEDIUM);

        Image image = uiComponents.create(Image.class);
        image.setWidth("50px");
        image.setHeight("50px");
        image.setSrc("/icons/currency-default-transparent.png");

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(false);
        infoLayout.setSpacing(false);

        infoLayout.add(new H4(currency.getTicker()));
        infoLayout.add(new Text(currency.getName()));

        Button removeButton = new Button();
        removeButton.setIcon(new Icon(VaadinIcon.CLOSE));
        removeButton.addThemeNames("tertiary", "error");
        removeButton.addSingleClickListener(buttonClickEvent -> {
            cryptocurrenciesDc.getMutableItems().remove(currency);
            settingsChanged();
        });

        cardLayout.add(image, infoLayout, removeButton);
        return cardLayout;
    }

    /*
     * Protocols
     */

    @Supply(to = "protocolsList", subject = "renderer")
    private Renderer<DeFiProtocol> protocolsListRenderer() {
        return new ComponentRenderer<>(this::createProtocolRenderer);
    }

    @Subscribe(id = "protocolsDc", target = Target.DATA_CONTAINER)
    public void onProtocolsChange(CollectionContainer.CollectionChangeEvent<DeFiProtocol> event) {
        boolean isEmpty = event.getSource().getItems().isEmpty();
        protocolsListEmptyLabel.setVisible(isEmpty);
        protocolsList.setVisible(!isEmpty);
    }

    @Subscribe(id = "protocolAddBtn", subject = "clickListener")
    public void onProtocolAddAction(final ClickEvent<JmixButton> event) {
        dialogWindows.lookup(this, DeFiProtocol.class)
                .withLookupComponentMultiSelect(true)
                .withSelectHandler(protocols -> {
                    protocolsDc.getMutableItems().addAll(protocols);
                    settingsChanged();
                }).open();
    }

    private Component createProtocolRenderer(DeFiProtocol protocol) {
        HorizontalLayout cardLayout = uiComponents.create(HorizontalLayout.class);
        cardLayout.setMargin(false);
        cardLayout.addClassNames(LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST_10,
                LumoUtility.BorderRadius.MEDIUM);

        Image image = uiComponents.create(Image.class);
        image.setWidth("50px");
        image.setHeight("50px");
        image.setSrc("/icons/protocol-default-transparent.png");

        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setPadding(false);
        infoLayout.setSpacing(false);

        infoLayout.add(new H4(protocol.getName()));
        infoLayout.add(new Text(protocol.getDescription()));

        Button removeButton = new Button();
        removeButton.setIcon(new Icon(VaadinIcon.CLOSE));
        removeButton.addThemeNames("tertiary", "error");
        removeButton.addSingleClickListener(buttonClickEvent -> {
            protocolsDc.getMutableItems().remove(protocol);
            settingsChanged();
        });

        cardLayout.add(image, infoLayout, removeButton);
        return cardLayout;
    }

    private void settingsChanged() {
        NotificationSettings entity = getEditedEntity();

        List<Cryptocurrency> cryptocurrencies = ListUtils.emptyIfNull(entity.getSubscribedCryptocurrencies());
        List<DeFiProtocol> protocols = ListUtils.emptyIfNull(entity.getSubscribedDeFiProtocols());

        if (!ListUtils.isEqualList(initCryptocurrencies, cryptocurrencies)
            || !ListUtils.isEqualList(initProtocols, protocols)) {
            detailActions.setVisible(true);
        } else {
            detailActions.setVisible(false);
        }
    }

}