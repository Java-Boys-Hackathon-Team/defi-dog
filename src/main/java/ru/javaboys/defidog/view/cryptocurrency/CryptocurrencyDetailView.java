package ru.javaboys.defidog.view.cryptocurrency;

import com.vaadin.flow.router.Route;

import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "cryptocurrencies/:id", layout = MainView.class)
@ViewController(id = "Cryptocurrency.detail")
@ViewDescriptor(path = "cryptocurrency-detail-view.xml")
@EditedEntityContainer("cryptocurrencyDc")
public class CryptocurrencyDetailView extends StandardDetailView<Cryptocurrency> {
}