package ru.javaboys.defidog.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@JmixEntity
@Table(name = "TELEGRAM_USER", indexes = {
        @Index(name = "IDX_TELEGRAM_USER_UNQ", columnList = "TELEGRAM_USER_ID", unique = true)
})
@Entity
public class TelegramUser {

    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "TELEGRAM_USER_FIRST_NAME")
    private String telegramUserFirstName;

    @Column(name = "TELEGRAM_USER_LAST_NAME")
    private String telegramUserLastName;

    @Column(name = "TELEGRAM_USER_ID")
    private Long telegramUserId;

    @Column(name = "TELEGRAM_CHAT_ID")
    private Long telegramChatId;

    @InstanceName
    @Column(name = "TELEGRAM_USER_NAME")
    private String telegramUserName;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "telegramUser")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}