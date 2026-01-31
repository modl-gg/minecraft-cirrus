package dev.simplix.cirrus.menus;

import dev.simplix.cirrus.Cirrus;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.model.SearchConversation;
import dev.simplix.cirrus.service.SearchConversationHandleService;
import dev.simplix.protocolize.api.chat.ChatElement;
import dev.simplix.protocolize.data.ItemType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSearchableBrowser<T> extends AbstractBrowser<T> {

  protected AbstractSearchableBrowser() {
    registerActionHandler("Search", (
        apply -> {
          Cirrus
              .service(SearchConversationHandleService.class)
              .handle(apply.player(), this, searchConversation());
        }));
  }

  // ----------------------------------------------------------------------------------------------------
  // Implementation
  // ----------------------------------------------------------------------------------------------------

  @Override
  protected final Map<Integer, CirrusItem> intercept(int menuSize) {
    Map<Integer, CirrusItem> result = new HashMap<>(intercept0(menuSize));

    // Add search button at the last slot
    int searchSlot = menuSize - 1;
    result.put(searchSlot, searchItem());

    return result;
  }

  // ----------------------------------------------------------------------------------------------------
  // Abstract methods
  // ----------------------------------------------------------------------------------------------------

  public abstract void redisplay(Collection<T> content);

  public abstract Collection<T> searchByPartialString(String partial);

  protected abstract SearchConversation searchConversation();

  // ----------------------------------------------------------------------------------------------------
  // Methods that might be overridden
  // ----------------------------------------------------------------------------------------------------

  /**
   * Override this method to add additional intercepted slots.
   *
   * @param menuSize the total size of the menu
   * @return a map of slot index to CirrusItem
   */
  protected Map<Integer, CirrusItem> intercept0(int menuSize) {
    return Map.of();
  }

  protected CirrusItem searchItem() {
    return CirrusItem
        .of(ItemType.COMPASS)
        .displayName(ChatElement.ofLegacyText("&6Search"))
        .lore(compassLore())
        .actionHandler("Search");
  }

  /**
   * Defines the lore our compass should have
   */
  protected ChatElement<?>[] compassLore() {
    return new ChatElement[]{
        ChatElement.ofLegacyText("&7Search for a value"),
    };
  }

}
