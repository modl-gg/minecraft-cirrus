package dev.simplix.cirrus.velocity.menubuilder;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.protocol.nbt.NBT;
import com.github.retrooper.packetevents.protocol.nbt.NBTByte;
import com.github.retrooper.packetevents.protocol.nbt.NBTByteArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTDouble;
import com.github.retrooper.packetevents.protocol.nbt.NBTFloat;
import com.github.retrooper.packetevents.protocol.nbt.NBTInt;
import com.github.retrooper.packetevents.protocol.nbt.NBTIntArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTLong;
import com.github.retrooper.packetevents.protocol.nbt.NBTLongArray;
import com.github.retrooper.packetevents.protocol.nbt.NBTShort;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.nbt.NBTType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import com.velocitypowered.api.proxy.Player;
import dev.simplix.cirrus.actionhandler.ActionHandler;
import dev.simplix.cirrus.inventory.InventoryTracker;
import dev.simplix.cirrus.inventory.InventoryTracker.TrackedInventory;
import dev.simplix.cirrus.item.CirrusBaseItemStack;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import dev.simplix.cirrus.menu.DisplayedMenu;
import dev.simplix.cirrus.menu.Menu;
import dev.simplix.cirrus.menu.Menus;
import dev.simplix.cirrus.model.CirrusClickType;
import dev.simplix.cirrus.model.Click;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import dev.simplix.cirrus.service.MenuBuildService;
import dev.simplix.cirrus.text.CirrusChatElement;
import dev.simplix.cirrus.velocity.util.ComponentHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.querz.nbt.tag.*;

@Slf4j
public class VelocityMenuBuildService implements MenuBuildService {

    @Getter
    private final InventoryTracker inventoryTracker = new InventoryTracker();
    private final Set<Long> usedIDs = new HashSet<>();

    @Override
    public DisplayedMenu openAndBuildMenu0(Menu menu, CirrusPlayerWrapper playerWrapper) {
        Player player = playerWrapper.handle();
        UUID playerUuid = player.getUniqueId();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        
        int windowId = inventoryTracker.generateWindowId(playerUuid);
        long id = generateID();

        CirrusInventoryType invType = menu.type();
        int size = invType.size();

        CirrusChatElement titleElement = CirrusChatElement.ofLegacyText(menu.title() != null ? menu.title() : "");
        Component title = ComponentHelper.removeItalic(titleElement.asComponent());

        CirrusBaseItemStack[] items = new CirrusBaseItemStack[size];
        menu.rootItems().forEach((slot, item) -> {
            if (slot >= 0 && slot < size) {
                items[slot] = item;
            }
        });

        DisplayedMenu displayedMenu = new DisplayedMenu(menu, windowId, playerWrapper, this, id);

        TrackedInventory tracked = new TrackedInventory(
            windowId,
            invType,
            titleElement,
            items,
            displayedMenu
        );

        inventoryTracker.track(playerUuid, windowId, tracked);

        sendOpenWindow(user, windowId, invType, title);
        sendWindowItems(user, windowId, items, playerWrapper.protocolVersion());

        return displayedMenu;
    }

    @Override
    public void updateMenu(DisplayedMenu displayedMenu) {
        if (displayedMenu.closed().get()) {
            return;
        }

        Player player = displayedMenu.player().handle();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);
        UUID playerUuid = player.getUniqueId();
        int windowId = (int) displayedMenu.nativeMenu();

        Optional<TrackedInventory> trackedOpt = inventoryTracker.get(playerUuid, windowId);
        if (trackedOpt.isEmpty()) {
            buildAndOpenMenu(displayedMenu.value(), displayedMenu.player());
            return;
        }

        TrackedInventory tracked = trackedOpt.get();
        Menu menu = displayedMenu.value();

        if (tracked.type() != menu.type()) {
            buildAndOpenMenu(menu, displayedMenu.player());
            return;
        }

        CirrusBaseItemStack[] items = tracked.items();
        menu.rootItems().forEach((slot, item) -> {
            if (slot >= 0 && slot < items.length) {
                items[slot] = item;
            }
        });

        sendWindowItems(user, windowId, items, displayedMenu.player().protocolVersion());
    }

    @Override
    public void closeMenu0(DisplayedMenu displayedMenu) {
        Player player = displayedMenu.player().handle();
        User user = PacketEvents.getAPI().getPlayerManager().getUser(player);

        int windowId = (int) displayedMenu.nativeMenu();
        user.sendPacket(new WrapperPlayServerCloseWindow(windowId));

        inventoryTracker.untrack(player.getUniqueId(), windowId);
    }

    public void handleClick(TrackedInventory tracked, int slot, CirrusClickType clickType) {
        DisplayedMenu displayedMenu = tracked.displayedMenu();
        if (displayedMenu == null || displayedMenu.closed().get()) {
            return;
        }

        Menu menu = displayedMenu.value();
        Optional<ActionHandler> handlerOpt = menu.actionHandler(slot);

        if (handlerOpt.isEmpty()) {
            return;
        }

        CirrusBaseItemStack clickedItem = slot >= 0 && slot < tracked.items().length
            ? tracked.items()[slot]
            : null;

        Click click = new Click(clickType, displayedMenu, clickedItem, slot);

        try {
            handlerOpt.get().handle(click);
        } catch (Exception e) {
            log.warn("Exception caught in click handler", e);
        }
    }

    public void handleClose(UUID playerUuid, TrackedInventory tracked) {
        DisplayedMenu displayedMenu = tracked.displayedMenu();
        if (displayedMenu != null) {
            displayedMenu.closed().set(true);
        }
        Menus.remove(playerUuid);
    }

    private void sendOpenWindow(User user, int windowId, CirrusInventoryType type, Component title) {
        WrapperPlayServerOpenWindow packet = new WrapperPlayServerOpenWindow(
            windowId,
            type.toPacketEventsTypeId(),
            title
        );
        user.sendPacket(packet);
    }

    private void sendWindowItems(User user, int windowId, CirrusBaseItemStack[] items, int protocolVersion) {
        List<ItemStack> packetItems = new ArrayList<>();

        for (CirrusBaseItemStack item : items) {
            if (item == null) {
                packetItems.add(ItemStack.EMPTY);
            } else {
                packetItems.add(toPacketEventsItemStack(item, protocolVersion));
            }
        }

        WrapperPlayServerWindowItems packet = new WrapperPlayServerWindowItems(windowId, 0, packetItems, null);
        user.sendPacket(packet);
    }

    private ItemStack toPacketEventsItemStack(CirrusBaseItemStack cirrusItem, int protocolVersion) {
        CirrusItemType cirrusType = cirrusItem.itemType();

        ItemType itemType = ItemTypes.getByName(cirrusType.identifier());
        if (itemType == null) {
            log.warn("Unknown item type: {}", cirrusType.identifier());
            return ItemStack.EMPTY;
        }

        ItemStack.Builder builder = ItemStack.builder()
            .type(itemType)
            .amount(cirrusItem.amount());

        CirrusChatElement displayName = cirrusItem.displayName();
        if (displayName != null && !displayName.isEmpty()) {
            Component nameComponent = ComponentHelper.removeItalic(displayName.asComponent());
            builder.component(ComponentTypes.CUSTOM_NAME, nameComponent);
        }

        List<CirrusChatElement> lore = cirrusItem.lore();
        if (lore != null && !lore.isEmpty()) {
            List<Component> loreComponents = lore.stream()
                .map(element -> ComponentHelper.removeItalic(element.asComponent()))
                .toList();
            builder.component(ComponentTypes.LORE, new ItemLore(loreComponents));
        }

        CompoundTag nbtData = cirrusItem.nbtData();
        if (nbtData != null && !nbtData.keySet().isEmpty()) {
            NBTCompound peNbt = convertNbt(nbtData);
            if (peNbt != null && !peNbt.getTags().isEmpty()) {
                builder.component(ComponentTypes.CUSTOM_DATA, peNbt);
            }
        }

        return builder.build();
    }

    private NBTCompound convertNbt(CompoundTag querz) {
        if (querz == null) {
            return null;
        }
        NBTCompound result = new NBTCompound();
        for (String key : querz.keySet()) {
            Tag<?> tag = querz.get(key);
            NBT peTag = convertTag(tag);
            if (peTag != null) {
                result.setTag(key, peTag);
            }
        }
        return result;
    }

    private NBT convertTag(Tag<?> tag) {
        if (tag == null) {
            return null;
        }

        return switch (tag.getID()) {
            case 1 -> new NBTByte(((ByteTag) tag).asByte());
            case 2 -> new NBTShort(((ShortTag) tag).asShort());
            case 3 -> new NBTInt(((IntTag) tag).asInt());
            case 4 -> new NBTLong(((LongTag) tag).asLong());
            case 5 -> new NBTFloat(((FloatTag) tag).asFloat());
            case 6 -> new NBTDouble(((DoubleTag) tag).asDouble());
            case 7 -> new NBTByteArray(((ByteArrayTag) tag).getValue());
            case 8 -> new NBTString(((StringTag) tag).getValue());
            case 9 -> convertListTag((ListTag<?>) tag);
            case 10 -> convertNbt((CompoundTag) tag);
            case 11 -> new NBTIntArray(((IntArrayTag) tag).getValue());
            case 12 -> new NBTLongArray(((LongArrayTag) tag).getValue());
            default -> null;
        };
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private NBTList convertListTag(ListTag<?> listTag) {
        NBTList result = new NBTList(NBTType.COMPOUND);

        for (Object element : listTag) {
            NBT converted = convertTag((Tag<?>) element);
            if (converted != null) {
                result.addTag(converted);
            }
        }
        return result;
    }

    private Long generateID() {
        long id = 0;
        while (usedIDs.contains(id)) {
            id++;
        }
        usedIDs.add(id);
        return id;
    }
}
