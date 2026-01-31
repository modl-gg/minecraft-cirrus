package dev.simplix.cirrus.menus.example;

import com.google.common.collect.Iterators;
import dev.simplix.cirrus.actionhandler.ActionHandlers;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import dev.simplix.cirrus.menus.SimpleMenu;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NextMenu extends SimpleMenu {

    private final Iterator<CirrusItemType> iterator = Iterators.cycle(
        CirrusItemType.STONE,
        CirrusItemType.EMERALD_BLOCK,
        CirrusItemType.BONE_BLOCK,
        CirrusItemType.IRON_BLOCK,
        CirrusItemType.FIRE_CORAL_BLOCK,
        CirrusItemType.COPPER_BLOCK);

    public NextMenu() {
        title("Next");
        type(CirrusInventoryType.GENERIC_9X5);

        set(CirrusItem.of(iterator.next(), CirrusChatElement.ofLegacyText("§aClick")).slot(9 * 2 + 4).actionHandler("click"));

        row(5).get(8).set(CirrusItem.of(CirrusItemType.DARK_OAK_DOOR, CirrusChatElement.ofLegacyText("§7Back")).actionHandler("back"));

    }

    @Override
    protected void registerActionHandlers() {
        registerActionHandler("back", ActionHandlers.openMenu(new SelectMenu()));

        registerActionHandler(
            "click",
            ActionHandlers.changeClickedItemType(iterator::next));
    }
}
