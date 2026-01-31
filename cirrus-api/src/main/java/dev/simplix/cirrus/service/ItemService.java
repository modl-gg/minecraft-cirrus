package dev.simplix.cirrus.service;

import dev.simplix.cirrus.item.CirrusItemType;

public class ItemService {

    public boolean isItemAvailable(CirrusItemType itemType, int protocolVersion) {
        // Item availability checking will be handled by platform implementations
        // For now, assume all items are available
        return itemType != null;
    }
}
