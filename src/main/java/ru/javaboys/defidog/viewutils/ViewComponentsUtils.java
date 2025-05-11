package ru.javaboys.defidog.viewutils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.function.Function;

public class ViewComponentsUtils {

    public static <T> Component createImageComponent(T item, Function<T, byte[]> imageExtractor) {
        byte[] logoImage = imageExtractor.apply(item);
        return getComponent(logoImage);
    }

    @NotNull
    private static Component getComponent(byte[] logoImage) {
        if (logoImage != null) {
            // Convert byte array to a Base64 string
            String base64Image = Base64.getEncoder().encodeToString(logoImage);
            // Create an Image component with the Base64 string
            Image image = new Image("data:image/png;base64," + base64Image, "Logo");
            image.setWidth("30px"); // Set the desired width
            image.setHeight("30px"); // Set the desired height
            return image;
        }
        return new Image(); // Return an empty image if logoImage is null
    }

}
