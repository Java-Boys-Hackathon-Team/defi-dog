import cytoscape from 'cytoscape';

window.renderCytoscapeGraph = function (container, rawJson) {
  if (!container || !rawJson) return;

  container.innerHTML = "";

  // Обновлённая визуальная обводка
  container.style.border = "2px solid rgba(0, 116, 217, 0.7)";
  container.style.borderRadius = "12px";
  container.style.boxShadow = "0 0 10px rgba(0, 116, 217, 0.4)";
  container.style.height = "100%";
  container.style.position = "relative";

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
          "text-valign": "top", // <-- Текст сверху
          "text-halign": "center",
          "text-margin-y": "-10px",
          "font-size": "14px",
          "font-weight": "bold",
          "text-outline-width": 2,
          "text-outline-color": "#0074D9"
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

  // Обработка клика по узлу
  cy.on("tap", "node", function (evt) {
    const node = evt.target;
    const uuid = node.data("id");

    console.log("[click] node UUID:", uuid);

    if (container.$server && container.$server.onNodeClick) {
      container.$server.onNodeClick(uuid);
    }
  });

  // Кнопки масштабирования
  const zoomControls = document.createElement("div");
  zoomControls.style.position = "absolute";
  zoomControls.style.top = "10px";
  zoomControls.style.right = "10px";
  zoomControls.style.display = "flex";
  zoomControls.style.flexDirection = "column";
  zoomControls.style.gap = "6px";
  zoomControls.style.zIndex = 10;

  const zoomInBtn = document.createElement("button");
  zoomInBtn.innerText = "+";
  zoomInBtn.style.padding = "4px 8px";
  zoomInBtn.style.fontSize = "16px";
  zoomInBtn.style.backgroundColor = "#0074D9";
  zoomInBtn.style.color = "white";
  zoomInBtn.style.border = "none";
  zoomInBtn.style.borderRadius = "6px";
  zoomInBtn.style.cursor = "pointer";

  const zoomOutBtn = document.createElement("button");
  zoomOutBtn.innerText = "−";
  zoomOutBtn.style.padding = "4px 8px";
  zoomOutBtn.style.fontSize = "16px";
  zoomOutBtn.style.backgroundColor = "#0074D9";
  zoomOutBtn.style.color = "white";
  zoomOutBtn.style.border = "none";
  zoomOutBtn.style.borderRadius = "6px";
  zoomOutBtn.style.cursor = "pointer";

  zoomInBtn.onclick = () => cy.zoom(cy.zoom() * 1.2);
  zoomOutBtn.onclick = () => cy.zoom(cy.zoom() * 0.8);

  zoomControls.appendChild(zoomInBtn);
  zoomControls.appendChild(zoomOutBtn);
  container.appendChild(zoomControls);
};