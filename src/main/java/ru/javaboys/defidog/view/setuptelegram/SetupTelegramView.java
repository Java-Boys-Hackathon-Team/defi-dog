package ru.javaboys.defidog.view.setuptelegram;


import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.vaadin.flow.router.Route;

import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.component.image.JmixImage;
import io.jmix.flowui.view.StandardView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import ru.javaboys.defidog.entity.ChannelEnum;
import ru.javaboys.defidog.entity.CodeEntity;
import ru.javaboys.defidog.entity.User;
import ru.javaboys.defidog.integrations.telegram.TelegramBotService;
import ru.javaboys.defidog.utils.CodeUtil;
import ru.javaboys.defidog.utils.InMemoryImageSource;
import ru.javaboys.defidog.view.main.MainView;

@Route(value = "setup-telegram-view", layout = MainView.class)
@ViewController(id = "SetupTelegramView")
@ViewDescriptor(path = "setup-telegram-view.xml")
public class SetupTelegramView extends StandardView {

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private TelegramBotService telegramBotService;

    @ViewComponent
    private JmixImage<byte[]> qrImage;

    @Subscribe
    public void onInit(InitEvent event) throws Exception {

        CodeEntity codeEntity = createCode();
        String botName = telegramBotService.getBotName();
        String link = "https://t.me/" + botName + "?start=" + codeEntity.getCode();

        final BufferedImage bufferedImage = generateQR(link);
        qrImage.setValueSource(new InMemoryImageSource(bufferedImage));
    }

    private CodeEntity createCode() {
        String username = currentAuthentication.getUser().getUsername();
        User user = dataManager.load(User.class)
                .query("select u from User u where u.username = :username")
                .parameter("username", username)
                .one();

        String code = CodeUtil.randomCode();
        CodeEntity codeEntity = dataManager.create(CodeEntity.class);
        codeEntity.setType(ChannelEnum.TELEGRAM);
        codeEntity.setCode(code);
        codeEntity.setUser(user);

        return dataManager.save(codeEntity);
    }

    private BufferedImage generateQR(String text) throws WriterException {
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

}