package ru.javaboys.defidog.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "NOTIFICATION_SETTINGS", indexes = {
        @Index(name = "IDX_NOTIFICATION_SETTINGS_USER", columnList = "USER_ID")
})
@Entity
@Getter
@Setter
public class NotificationSettings {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @JoinColumn(name = "USER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinTable(name = "NOTIFICATION_SETTINGS_CRYPTOCURRENCY_LINK",
            joinColumns = @JoinColumn(name = "NOTIFICATION_SETTINGS_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "CRYPTOCURRENCY_ID", referencedColumnName = "ID"))
    @ManyToMany
    private List<Cryptocurrency> subscribedCryptocurrencies;

    @JoinTable(name = "NOTIFICATION_SETTINGS_DE_FI_PROTOCOL_LINK",
            joinColumns = @JoinColumn(name = "NOTIFICATION_SETTINGS_ID"),
            inverseJoinColumns = @JoinColumn(name = "DE_FI_PROTOCOL_ID"))
    @ManyToMany
    private List<DeFiProtocol> subscribedDeFiProtocols;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;
}