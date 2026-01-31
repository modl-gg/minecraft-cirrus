package dev.simplix.cirrus.common.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

@UtilityClass
public class ComponentHelper {

    public Component removeItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, State.FALSE);
    }
}
