package dev.simplix.cirrus.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.simplix.cirrus.effect.AbstractMenuEffect;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class CirrusItemSerializer implements JsonSerializer<CirrusItem> {

    @Override
    public JsonElement serialize(
        CirrusItem src,
        Type typeOfSrc,
        JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.add(
            RuntimeTypeAdapterFactory.TYPE,
            new JsonPrimitive(src.getClass().getSimpleName().toLowerCase()));

        final String displayName = src.displayName() == null ? null : src.displayName().asLegacyText();
        AbstractMenuEffect<String> effect = src.displayNameEffect();
        if (displayName != null && effect == null) {
            jsonObject.add("display-name", new JsonPrimitive(displayName));
        }

        if (effect != null) {
            jsonObject.add("display-name-effect", context.serialize(effect, AbstractMenuEffect.class));
        }

        if (src.itemType() != null) {
            jsonObject.add("type", new JsonPrimitive(src.itemType().identifier()));
        }
        final List<String> lores = src.lore().stream()
            .map(CirrusChatElement::asLegacyText)
            .collect(Collectors.toList());
        jsonObject.add("lore", lores.isEmpty() ? new JsonArray() : context.serialize(lores));
        jsonObject.add("amount", new JsonPrimitive(src.amount()));
        jsonObject.add("durability", new JsonPrimitive(src.durability()));
        jsonObject.add("hide-flags", new JsonPrimitive(src.hideFlags()));

        jsonObject.add(
            "action-handler",
            new JsonPrimitive(src.actionHandler() == null ? "noAction" : src.actionHandler()));
        jsonObject.add("flags", context.serialize(src.itemFlags()));

        if (src.actionArguments().isEmpty()) {
            jsonObject.add("action-arguments", new JsonArray());
        } else {
            jsonObject.add("action-arguments", context.serialize(src.actionArguments()));
        }

        return jsonObject;
    }
}
