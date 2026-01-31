package dev.simplix.cirrus.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.querz.nbt.tag.CompoundTag;

public class ItemStackDeserializer implements JsonDeserializer<CirrusBaseItemStack> {

    @Override
    public CirrusBaseItemStack deserialize(
        JsonElement json,
        Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {

        final JsonObject asJsonObject = json.getAsJsonObject();
        final String typeStr = asJsonObject.get("type").getAsString();
        final CirrusItemType type = CirrusItemType.of(typeStr);
        final int amount = asJsonObject.get("amount").getAsInt();
        final short durability = asJsonObject.get("durability").getAsShort();
        final int hideflags = asJsonObject.get("hide-flags").getAsInt();
        final JsonElement nbtRaw = asJsonObject.get("nbt");
        final CompoundTag nbt = nbtRaw == null || nbtRaw.isJsonNull()
                                ? new CompoundTag()
                                : context.deserialize(nbtRaw, CompoundTag.class);
        final String displayName = asJsonObject.get("display-name") != null
                                   ? asJsonObject.get("display-name").getAsString()
                                   : "";
        @SuppressWarnings("unchecked")
        final List<String> loreStrings = asJsonObject.get("lore") != null
                                         ? context.deserialize(asJsonObject.get("lore"), List.class)
                                         : new ArrayList<>();

        final CirrusItem item = new CirrusItem(type, (byte) amount, durability)
            .displayName(CirrusChatElement.ofLegacyText(displayName))
            .nbtData(nbt)
            .hideFlags(hideflags);

        if (loreStrings != null) {
            item.lore(loreStrings.stream()
                .map(CirrusChatElement::ofLegacyText)
                .toList());
        }

        return item;
    }
}
