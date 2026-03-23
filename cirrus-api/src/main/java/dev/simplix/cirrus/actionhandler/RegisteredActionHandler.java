package dev.simplix.cirrus.actionhandler;

/**
 * A simple data class representing a registered `ActionHandler`.
 * This class associates a unique `String` name with an `ActionHandler` instance. It is used to
 * register `ActionHandler`s with the `ActionHandlerRegistry` so that they can be easily accessed
 * and used by menu items.
 */
public final class RegisteredActionHandler {

    private final String name;
    private final ActionHandler handler;

    public RegisteredActionHandler(String name, ActionHandler handler) {
        this.name = name;
        this.handler = handler;
    }

    public String name() {
        return this.name;
    }

    public ActionHandler handler() {
        return this.handler;
    }
}
