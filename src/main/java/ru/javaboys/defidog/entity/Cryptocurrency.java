package ru.javaboys.defidog.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.Comment;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "CRYPTOCURRENCY", indexes = {
        @Index(name = "IDX_CRYPTOCURRENCY_DEPENDENCY_GRAPH", columnList = "DEPENDENCY_GRAPH_ID")
})
@Entity
@Getter
@Setter
public class Cryptocurrency {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Comment("Название криптовалюты")
    @Column(name = "NAME")
    private String name;

    @Comment("Кракткое название криптовалютного инструмента")
    @Column(name = "TICKER")
    private String ticker;

    @Comment("Рыночной цена")
    @Column(name = "PRICE", precision = 19, scale = 10)
    private BigDecimal price;

    @Comment("Рыночная капитализация, общая стоимость всех токенов в обращении")
    @Column(name = "MARKET_CAP", precision = 24, scale = 2)
    private BigDecimal marketCap;

    @Comment("Общее кол-во существующих токенов")
    @Column(name = "TOTAL_SUPPLY", precision = 19, scale = 2)
    private BigDecimal totalSupply;

    @OneToMany(mappedBy = "cryptocurrency")
    private List<SmartContract> contracts;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @JoinColumn(name = "DEPENDENCY_GRAPH_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private ContractDependenciesGraph dependencyGraph;
}
