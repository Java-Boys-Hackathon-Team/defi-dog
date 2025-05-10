package ru.javaboys.defidog.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.Comment;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
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

    @Column(name = "CMC_ID")
    private Integer cmcId;

    @Column(name = "CMC_RANK")
    private Integer cmcRank;

    @InstanceName
    @Comment("Название криптовалюты")
    @Column(name = "NAME")
    private String name;

    @Comment("Кракткое название криптовалютного инструмента")
    @Column(name = "TICKER")
    private String ticker;

    @Comment("Рыночной цена")
    @Column(name = "PRICE", precision = 24, scale = 16)
    private BigDecimal price;

    @Column(name = "PERCENT_CHANGE24H", precision = 19, scale = 2)
    private BigDecimal percentChange24h;

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


    public Integer getCmcRank() {
        return cmcRank;
    }

    public void setCmcRank(Integer cmcRank) {
        this.cmcRank = cmcRank;
    }

    public Integer getCmcId() {
        return cmcId;
    }

    public void setCmcId(Integer cmcId) {
        this.cmcId = cmcId;
    }

    @JoinColumn(name = "DEPENDENCY_GRAPH_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private ContractDependenciesGraph dependencyGraph;

    public BigDecimal getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(BigDecimal percentChange24h) {
        this.percentChange24h = percentChange24h;
    }
}
