package ru.javaboys.defidog.audit.components;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.util.function.Consumer;

@Tag("div")
@NpmPackage(value = "cytoscape", version = "3.26.0")
@JsModule("js/dependency-graph.js")
public class GraphPanel extends Component {

    private Consumer<String> nodeClickHandler;

    public void setGraphJson(String json) {
        getElement().executeJs("window.renderCytoscapeGraph($0, $1);", getElement(), json);
    }

    public void setOnNodeClick(Consumer<String> handler) {
        this.nodeClickHandler = handler;
    }

    @ClientCallable
    public void onNodeClick(String nodeId) {
        if (nodeClickHandler != null) {
            nodeClickHandler.accept(nodeId);
        }
    }
}
