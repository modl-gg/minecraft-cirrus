package dev.simplix.cirrus.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.simplix.cirrus.effect.AbstractMenuEffect;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CirrusItemDeserializer implements JsonDeserializer<CirrusItem> {

    @Override
    @SuppressWarnings("unchecked")
    public CirrusItem deserialize(
        JsonElement jsonElement,
        Type typeOf,
        JsonDeserializationContext context) throws JsonParseException {

        final JsonObject asJsonObject = jsonElement.getAsJsonObject();
        final String typeStr = asJsonObject.get("type").getAsString();
        final CirrusItemType type = CirrusItemType.of(typeStr);
        final byte amount = asJsonObject.get("amount").getAsByte();
        final short durability = asJsonObject.get("durability").getAsShort();
        final int hideflags = asJsonObject.get("hide-flags").getAsInt();

        final AbstractMenuEffect<String> effect = asJsonObject.get("display-name-effect") != null
                                                  ? context.deserialize(asJsonObject.get("display-name-effect"), AbstractMenuEffect.class)
                                                  : null;

        String displayName = null;
        if (effect == null && asJsonObject.get("display-name") != null) {
            displayName = asJsonObject.get("display-name").getAsString();
        }

        final String actionHandler = asJsonObject.get("action-handler") != null
                                     ? asJsonObject.get("action-handler").getAsString()
                                     : "noAction";

        final List<String> lore = asJsonObject.get("lore") != null
                                  ? context.deserialize(asJsonObject.get("lore"), List.class)
                                  : new ArrayList<>();

        final List<String> actionArguments = asJsonObject.get("action-arguments") != null
                                             ? context.deserialize(asJsonObject.get("action-arguments"), List.class)
                                             : new ArrayList<>();

        CirrusItem item = new CirrusItem(type)
            .amount(amount)
            .displayNameEffect(effect)
            .durability(durability)
            .hideFlags(hideflags)
            .actionHandler(actionHandler);

        if (displayName != null) {
            item.displayName(CirrusChatElement.ofLegacyText(displayName));
        }

        if (actionArguments != null) {
            item.actionArguments(actionArguments);
        }

        if (lore != null) {
            item.loreElements(lore.stream()
                .map(CirrusChatElement::ofLegacyText)
                .toList());
        }

        return item;
    }
}
