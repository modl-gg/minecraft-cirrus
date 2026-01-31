package dev.simplix.cirrus.menus.example;

import dev.simplix.cirrus.actionhandler.ActionHandlers;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.item.Items;
import dev.simplix.cirrus.menus.SimpleMenu;
import dev.simplix.cirrus.text.CirrusChatElement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SelectMenu extends SimpleMenu {

    public SelectMenu() {
        title("select item");

        set(Items.withSpectrumEffect(
                CirrusItemType.STONE, "Next Menu",
                CirrusChatElement.ofLegacyText("§7Click here to go"),
                CirrusChatElement.ofLegacyText("§7to the next menu"))
            .slot(12)
            .actionHandler("next")
        );

        set(Items.withSpectrumEffect(
                CirrusItemType.STONE,
                "Item Type Browser",
                CirrusChatElement.ofLegacyText("§7Click here to go"),
                CirrusChatElement.ofLegacyText("§7to the item-type menu"))
            .actionHandler("browser")
            .slot(14)
        );
    }

    @Override
    protected void registerActionHandlers() {
        registerActionHandler("next", ActionHandlers.openMenu(new NextMenu()));
        registerActionHandler(
            "browser",
            ActionHandlers.openMenu(new ItemTypeBrowser(ClientVersion.V_1_17.getProtocolVersion())));
    }

    @Override
    public int updateTicks() {
        return 2;
    }
}