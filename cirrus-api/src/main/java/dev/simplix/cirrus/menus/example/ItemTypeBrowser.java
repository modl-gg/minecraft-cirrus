package dev.simplix.cirrus.menus.example;

import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.actionhandler.ActionHandlers;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.Items;
import dev.simplix.cirrus.menus.AbstractBrowser;
import dev.simplix.cirrus.model.Click;
import dev.simplix.cirrus.service.ItemService;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.data.ItemType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemTypeBrowser extends AbstractBrowser<ItemType> {

  private final int protocolVersion;

  public ItemTypeBrowser(int protocolVersion) {
    this.protocolVersion = protocolVersion;
    title("§7Item Browser");
  }

  @Override
  protected int updateTicks() {
    return 2;
  }

  @Override
  protected void registerActionHandlers() {
    registerActionHandler("back", ActionHandlers.openMenu(new SelectMenu()));
  }

  @Override
  protected Map<Integer, CirrusItem> intercept(int menuSize) {
    // Intercept the last slot in the bottom row for back button
    int backButtonSlot = menuSize - 1;
    return Map.of(
        backButtonSlot,
        CirrusItem
            .of(
                ItemType.ACACIA_DOOR,
                ChatElement.ofLegacyText("§7Back"),
                ChatElement.ofLegacyText("§7Go back to the previous menu"))
            .actionHandler("back")
    );
  }

  @Override
  protected void handleClick(Click click, ItemType value) {
    click.player().sendMessage("§7You clicked on " + value.name());
  }

  @Override
  protected Collection<ItemType> elements() {
    return Arrays.stream(ItemType.values()).filter(type -> Cirrus.service(ItemService.class).isItemAvailable(type, protocolVersion)).toList();
  }

  @Override
  protected CirrusItem map(ItemType element) {
    return Items.withWaveEffect(element, element.name());
  }
}
