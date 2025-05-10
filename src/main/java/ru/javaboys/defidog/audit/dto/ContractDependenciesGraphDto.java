package ru.javaboys.defidog.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractDependenciesGraphDto {
    private Elements elements;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Elements {
        private List<Node> nodes;
        private List<Edge> edges;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Node {
        private Map<String, Object> data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edge {
        private Map<String, Object> data;
    }
}
