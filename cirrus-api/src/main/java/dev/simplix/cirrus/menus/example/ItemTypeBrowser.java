package dev.simplix.cirrus.menus.example;

import dev.simplix.cirrus.actionhandler.ActionHandlers;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.item.Items;
import dev.simplix.cirrus.menus.AbstractBrowser;
import dev.simplix.cirrus.model.Click;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemTypeBrowser extends AbstractBrowser<CirrusItemType> {

    private static final List<CirrusItemType> BROWSABLE_ITEMS = Arrays.asList(
        CirrusItemType.STONE,
        CirrusItemType.DIAMOND,
        CirrusItemType.EMERALD,
        CirrusItemType.GOLD_INGOT,
        CirrusItemType.IRON_INGOT,
        CirrusItemType.REDSTONE,
        CirrusItemType.COMPASS,
        CirrusItemType.CLOCK,
        CirrusItemType.PAPER,
        CirrusItemType.BOOK,
        CirrusItemType.CHEST,
        CirrusItemType.ENDER_CHEST,
        CirrusItemType.PLAYER_HEAD,
        CirrusItemType.NAME_TAG,
        CirrusItemType.HOPPER,
        CirrusItemType.EMERALD_BLOCK,
        CirrusItemType.BONE_BLOCK,
        CirrusItemType.IRON_BLOCK,
        CirrusItemType.COPPER_BLOCK
    );

    private final int protocolVersion;

    public ItemTypeBrowser(int protocolVersion) {
        this.protocolVersion = protocolVersion;
        title("§7Item Browser");
    }

    @Override
    protected void registerActionHandlers() {
        registerActionHandler("back", ActionHandlers.openMenu(new SelectMenu()));
    }

    @Override
    protected int updateTicks() {
        return 2;
    }

    @Override
    protected void handleClick(Click click, CirrusItemType value) {
        click.player().sendMessage("§7You clicked on " + value.name());
    }

    @Override
    protected Collection<CirrusItemType> elements() {
        return BROWSABLE_ITEMS;
    }

    @Override
    protected CirrusItem map(CirrusItemType element) {
        return Items.withWaveEffect(element, element.name());
    }

    @Override
    protected Map<Integer, CirrusItem> intercept(int menuSize) {
        int backButtonSlot = menuSize - 1;
        return Collections.singletonMap(
            backButtonSlot,
            CirrusItem
                .of(
                    CirrusItemType.ACACIA_DOOR,
                    CirrusChatElement.ofLegacyText("§7Back"),
                    CirrusChatElement.ofLegacyText("§7Go back to the previous menu"))
                .actionHandler("back")
        );
    }
}
