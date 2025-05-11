package ru.javaboys.defidog.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@JmixEntity
@Table(name = "CODE_ENTITY", indexes = {
        @Index(name = "IDX_CODE_ENTITY_USER", columnList = "USER_ID")
})
@Getter
@Setter
@Entity
public class CodeEntity {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @NotNull
    @Column(name = "CODE", length = 8)
    private String code;

    @NotNull
    @JoinColumn(name = "USER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @Column(name = "TYPE_")
    private String type;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    public ChannelEnum getType() {
        return type == null ? null : ChannelEnum.fromId(type);
    }

    public void setType(ChannelEnum type) {
        this.type = type == null ? null : type.getId();
    }

}