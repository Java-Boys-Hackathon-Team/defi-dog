package ru.javaboys.defidog.view.cryptocurrency;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.viewutils.DynamicListView;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "cryptocurrencies", layout = MainView.class)
@ViewController(id = "Cryptocurrency.list")
@ViewDescriptor(path = "cryptocurrency-list-view.xml")
@LookupComponent("cryptocurrenciesDataGrid")
@DialogMode(width = "64em")
public class CryptocurrencyListView extends StandardListView<Cryptocurrency> implements DynamicListView {

    @ViewComponent
    private CollectionLoader<Cryptocurrency> cryptocurrenciesDl;

    @Override
    public void activate() {
        cryptocurrenciesDl.load();
    }

}