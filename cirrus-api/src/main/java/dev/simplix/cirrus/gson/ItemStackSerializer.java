package dev.simplix.cirrus.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.lang.reflect.Type;
import java.util.List;

public class ItemStackSerializer implements JsonSerializer<CirrusBaseItemStack> {

    @Override
    public JsonElement serialize(
        CirrusBaseItemStack src,
        Type typeOfSrc,
        JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();

        jsonObject.add(
            RuntimeTypeAdapterFactory.TYPE,
            new JsonPrimitive(src.getClass().getSimpleName().toLowerCase()));

        if (src.itemType() != null) {
            jsonObject.add("type", new JsonPrimitive(src.itemType().identifier()));
        }
        jsonObject.add("amount", new JsonPrimitive(src.amount()));
        jsonObject.add("durability", new JsonPrimitive(src.durability()));
        jsonObject.add("hide-flags", new JsonPrimitive(src.hideFlags()));
        jsonObject.add("nbt", context.serialize(src.nbtData()));

        if (src.displayName() != null && !src.displayName().isEmpty()) {
            jsonObject.add("display-name", new JsonPrimitive(src.displayName().asLegacyText()));
        }

        final List<String> lores = src.lore().stream()
            .map(CirrusChatElement::asLegacyText)
            .toList();
        if (!lores.isEmpty()) {
            jsonObject.add("lore", context.serialize(lores));
        }

        jsonObject.add("flags", context.serialize(src.itemFlags()));

        return jsonObject;
    }
}
