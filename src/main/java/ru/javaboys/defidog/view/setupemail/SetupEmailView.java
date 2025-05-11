package ru.javaboys.defidog.view.setupemail;


import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.UiEventPublisher;
import io.jmix.flowui.action.DialogAction;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.ChannelEnum;
import ru.javaboys.defidog.entity.CodeEntity;
import ru.javaboys.defidog.entity.User;
import ru.javaboys.defidog.event.UserChannelUpdatedEvent;
import ru.javaboys.defidog.mail.MailService;
import ru.javaboys.defidog.util.CodeUtil;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "setup-email-view", layout = MainView.class)
@ViewController(id = "SetupEmailView")
@ViewDescriptor(path = "setup-email-view.xml")
public class SetupEmailView extends StandardView {

    @Autowired
    private Dialogs dialogs;

    @Autowired
    private UiEventPublisher eventPublisher;

    @Autowired
    private MailService mailService;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @ViewComponent
    private EmailField emailField;

    @ViewComponent
    private Button setupButton;

    @ViewComponent
    private Button clearButton;

    @ViewComponent
    private HorizontalLayout codeLayout;

    @ViewComponent
    private TextField codeField;

    @Subscribe(id = "setupButton", subject = "clickListener")
    public void onSetupButtonAction(final ClickEvent<JmixButton> event) {
        if (StringUtils.isNotBlank(emailField.getValue())) {
            setupButtons(false, true);
            codeLayout.setVisible(true);
            this.sendCode();
        }
    }

    @Subscribe(id = "clearButton", subject = "clickListener")
    public void onClearButtonAction(final ClickEvent<JmixButton> event) {
        setupButtons(true, false);
        codeLayout.setVisible(false);
    }

    @Subscribe(id = "codeConfirmButton", subject = "clickListener")
    public void onCodeConfirmButtonAction(final ClickEvent<JmixButton> event) {
        String username = currentAuthentication.getUser().getUsername();
        User user = dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .one();
        Optional<CodeEntity> codeEntity = dataManager.load(CodeEntity.class)
                .query("select c from CodeEntity c where c.user = :user and c.type = :type order by c.createdDate desc")
                .parameter("user", user)
                .parameter("type", ChannelEnum.EMAIL.name())
                .optional();

        if (codeEntity.isPresent() && codeEntity.get().getCode().equals(codeField.getValue())) {
            user.setEmail(emailField.getValue());
            dataManager.save(user);

            dialogs.createOptionDialog()
                    .withHeader("Код подтверждён")
                    .withContent(new Html("<p>Email успешно привязан.<br />" +
                                          "Теперь вы будете получать все важные уведомления на ваш email адрес</p>"))
                    .withActions(new DialogAction(DialogAction.Type.OK)
                            .withHandler(this::codeConfirmSuccess))
                    .open();
        } else {
            dialogs.createOptionDialog()
                    .withHeader("Неверный код")
                    .withText("Введен неверный код, попробуйте еще раз или запросите новый код")
                    .withActions(new DialogAction(DialogAction.Type.OK)
                            .withHandler(this::codeConfirmError))
                    .open();
        }
    }

    private void codeConfirmSuccess(ActionPerformedEvent e) {
        eventPublisher.publishEventForCurrentUI(new UserChannelUpdatedEvent(this));
    }

    private void codeConfirmError(ActionPerformedEvent e) {

    }

    private void setupButtons(boolean setup, boolean clear) {
        setupButton.setVisible(setup);
        clearButton.setVisible(clear);
        emailField.setEnabled(setup);
    }

    private void sendCode() {
        String username = currentAuthentication.getUser().getUsername();
        User user = dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .one();

        String code = CodeUtil.randomCode();
        CodeEntity codeEntity = dataManager.create(CodeEntity.class);
        codeEntity.setType(ChannelEnum.EMAIL);
        codeEntity.setCode(code);
        codeEntity.setUser(user);
        dataManager.save(codeEntity);

        mailService.sendEmailSetupCode(emailField.getValue(), code);
    }

}