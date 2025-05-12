package ru.javaboys.defidog.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.UiMinimalPolicies;
import io.jmix.securityflowui.role.annotation.ViewPolicy;
import ru.javaboys.defidog.entity.AbiChangeSet;
import ru.javaboys.defidog.entity.AuditReport;
import ru.javaboys.defidog.entity.CodeEntity;
import ru.javaboys.defidog.entity.ContractDependenciesGraph;
import ru.javaboys.defidog.entity.Cryptocurrency;
import ru.javaboys.defidog.entity.DeFiProtocol;
import ru.javaboys.defidog.entity.Notification;
import ru.javaboys.defidog.entity.NotificationSettings;
import ru.javaboys.defidog.entity.ScanTool;
import ru.javaboys.defidog.entity.SmartContract;
import ru.javaboys.defidog.entity.SourceCode;
import ru.javaboys.defidog.entity.SourceCodeChangeSet;
import ru.javaboys.defidog.entity.SourceCodeSecurityScanJob;
import ru.javaboys.defidog.entity.TelegramUser;
import ru.javaboys.defidog.entity.User;

@ResourceRole(name = "UI: minimal access", code = UiMinimalRole.CODE)
public interface UiMinimalRole extends UiMinimalPolicies {

    String CODE = "ui-minimal";

    @ViewPolicy(viewIds = {"MainView", "SettingsView", "SetupEmailView", "SetupTelegramView", "NotificationUserSettings.detail", "Cryptocurrency.list", "DeFiProtocol.list"})
    void main();

    @ViewPolicy(viewIds = "LoginView")
    @SpecificPolicy(resources = "ui.loginToUi")
    void login();

    @EntityAttributePolicy(entityClass = Cryptocurrency.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Cryptocurrency.class, actions = EntityPolicyAction.READ)
    void cryptocurrency();

    @EntityAttributePolicy(entityClass = DeFiProtocol.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = DeFiProtocol.class, actions = EntityPolicyAction.READ)
    void deFiProtocol();

    @EntityAttributePolicy(entityClass = User.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = User.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE})
    void user();

    @EntityAttributePolicy(entityClass = TelegramUser.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = TelegramUser.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void telegramUser();

    @EntityAttributePolicy(entityClass = ContractDependenciesGraph.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ContractDependenciesGraph.class, actions = EntityPolicyAction.READ)
    void contractDependenciesGraph();

    @EntityAttributePolicy(entityClass = CodeEntity.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = CodeEntity.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.CREATE, EntityPolicyAction.UPDATE})
    void codeEntity();

    @EntityAttributePolicy(entityClass = Notification.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = Notification.class, actions = EntityPolicyAction.READ)
    void notification();

    @EntityAttributePolicy(entityClass = NotificationSettings.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = NotificationSettings.class, actions = {EntityPolicyAction.READ, EntityPolicyAction.UPDATE, EntityPolicyAction.CREATE})
    void notificationSettings();

    @EntityAttributePolicy(entityClass = ScanTool.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ScanTool.class, actions = EntityPolicyAction.READ)
    void scanTool();

    @EntityAttributePolicy(entityClass = SourceCode.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = SourceCode.class, actions = EntityPolicyAction.READ)
    void sourceCode();

    @EntityAttributePolicy(entityClass = SmartContract.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = SmartContract.class, actions = EntityPolicyAction.READ)
    void smartContract();

    @EntityAttributePolicy(entityClass = SourceCodeChangeSet.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = SourceCodeChangeSet.class, actions = EntityPolicyAction.READ)
    void sourceCodeChangeSet();

    @EntityAttributePolicy(entityClass = SourceCodeSecurityScanJob.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = SourceCodeSecurityScanJob.class, actions = EntityPolicyAction.READ)
    void sourceCodeSecurityScanJob();

    @EntityAttributePolicy(entityClass = AbiChangeSet.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = AbiChangeSet.class, actions = EntityPolicyAction.READ)
    void abiChangeSet();

    @EntityPolicy(entityClass = AuditReport.class, actions = EntityPolicyAction.READ)
    @EntityAttributePolicy(entityClass = AuditReport.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    void auditReport();
}
