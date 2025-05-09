package ru.javaboys.defidog.repositories;

import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.FetchPlans;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ChangeSetRepository {

    private final DataManager dataManager;
    private final FetchPlans fetchPlans;

    public List<SourceCodeChangeSet> loadCodeChanges(UUID sourceId) {
        FetchPlan fetchPlan = fetchPlans.builder(SourceCodeChangeSet.class)
                .add("id")
                .add("changeSummary")
                .add("createdDate")
                .add("commitHash")
                .add("auditReport", b -> b
                        .add("id")
                        .add("summary")
                        .add("criticality"))
                .build();

        return dataManager.load(SourceCodeChangeSet.class)
                .query("select s from SourceCodeChangeSet s join fetch s.auditReport where s.sourceCode.id = :id order by s.createdDate desc")
                .parameter("id", sourceId)
                .fetchPlan(fetchPlan)
                .list();
    }

    public List<AbiChangeSet> loadAbiChanges(UUID sourceId) {
        FetchPlan fetchPlan = fetchPlans.builder(AbiChangeSet.class)
                .add("id")
                .add("changeSummary")
                .add("createdDate")
                .add("auditReport", b -> b
                        .add("id")
                        .add("summary")
                        .add("criticality"))
                .build();

        return dataManager.load(AbiChangeSet.class)
                .query("select a from AbiChangeSet a where a.sourceCode.id = :id order by a.createdDate desc")
                .parameter("id", sourceId)
                .fetchPlan(fetchPlan)
                .list();
    }
}
