package dev.simplix.cirrus.text;

import java.util.Objects;
import javax.annotation.Nullable;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class CirrusChatElement {

    private static final LegacyComponentSerializer LEGACY_SERIALIZER =
        LegacyComponentSerializer.legacySection();

    private final Component component;
    private String cachedLegacyText;

    private CirrusChatElement(@NonNull Component component) {
        this.component = component;
    }

    public static CirrusChatElement of(@NonNull Component component) {
        return new CirrusChatElement(component);
    }

    public static CirrusChatElement ofLegacyText(@Nullable String legacyText) {
        if (legacyText == null || legacyText.isEmpty()) {
            return new CirrusChatElement(Component.empty());
        }
        String colorized = legacyText.replace("&", "\u00a7");
        Component component = LEGACY_SERIALIZER.deserialize(colorized);
        CirrusChatElement element = new CirrusChatElement(component);
        element.cachedLegacyText = colorized;
        return element;
    }

    public static CirrusChatElement empty() {
        return new CirrusChatElement(Component.empty());
    }

    public Component asComponent() {
        return component;
    }

    public String asLegacyText() {
        if (cachedLegacyText == null) {
            cachedLegacyText = LEGACY_SERIALIZER.serialize(component);
        }
        return cachedLegacyText;
    }

    public boolean isEmpty() {
        return component.equals(Component.empty()) ||
               (cachedLegacyText != null && cachedLegacyText.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CirrusChatElement that = (CirrusChatElement) o;
        return Objects.equals(component, that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(component);
    }

    @Override
    public String toString() {
        return asLegacyText();
    }
}
