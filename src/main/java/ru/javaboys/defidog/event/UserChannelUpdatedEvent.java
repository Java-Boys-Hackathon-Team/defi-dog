package ru.javaboys.defidog.event;

import org.springframework.context.ApplicationEvent;

import io.jmix.flowui.view.StandardView;

public final class UserChannelUpdatedEvent extends ApplicationEvent {
    private final StandardView view;

    public UserChannelUpdatedEvent(StandardView view) {
        super(view);
        this.view = view;
    }

    public StandardView getView() {
        return view;
    }
}
