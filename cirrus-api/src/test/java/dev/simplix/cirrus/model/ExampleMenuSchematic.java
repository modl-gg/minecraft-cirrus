package dev.simplix.cirrus.model;

import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.item.Items;
import dev.simplix.cirrus.schematic.impl.SimpleMenuSchematic;
import dev.simplix.cirrus.text.CirrusChatElement;

public class ExampleMenuSchematic extends SimpleMenuSchematic {

    public ExampleMenuSchematic() {
        set(CirrusItem
            .of(CirrusItemType.ITEM_FRAME)
            .displayName(CirrusChatElement.ofLegacyText("item-1"))
            .lore(CirrusChatElement.ofLegacyText("test123"))
            .actionHandler("sheep")
            .slot(0));
        set(Items
            .withWaveEffect(CirrusItemType.ITEM_FRAME, "test", CirrusChatElement.ofLegacyText("test123"))
            .displayName(CirrusChatElement.ofLegacyText("item-2"))
            .lore(CirrusChatElement.ofLegacyText("test123"))
            .actionHandler("sheep")
            .slot(1));
        set(Items
            .withSpectrumEffect(CirrusItemType.ITEM_FRAME, "test", CirrusChatElement.ofLegacyText("test123"))
            .displayName(CirrusChatElement.ofLegacyText("item-3"))
            .lore(CirrusChatElement.ofLegacyText("test123"))
            .actionHandler("sheep")
            .slot(2));
    }
}
