import cytoscape from 'cytoscape';

window.renderCytoscapeGraph = function (container, rawJson) {
  if (!container) return;
  if (!rawJson) return;

  container.innerHTML = "";
  container.style.border = "2px dashed blue";
  container.style.height = "100%";

  let parsed;
  try {
    parsed = JSON.parse(rawJson);
  } catch (e) {
    console.error("Ошибка парсинга JSON:", e);
    return;
  }

  if (!parsed.elements) return;

  const cy = cytoscape({
    container: container,
    elements: parsed.elements,
    style: [
      {
        selector: "node",
        style: {
          "background-color": "#0074D9",
          "label": "data(label)",
          "color": "#fff",
          "text-valign": "center",
          "text-halign": "center",
          "font-size": "12px"
        }
      },
      {
        selector: "edge",
        style: {
          "width": 2,
          "line-color": "#ccc",
          "target-arrow-color": "#ccc",
          "target-arrow-shape": "triangle"
        }
      }
    ],
    layout: {
      name: "breadthfirst",
      directed: true,
      padding: 10
    }
  });

  cy.on("tap", "node", function (evt) {
    const node = evt.target;
    const uuid = node.data("id");

    console.log("[click] node UUID:", uuid);

    if (container.$server && container.$server.onNodeClick) {
      container.$server.onNodeClick(uuid);
    }
  });
};