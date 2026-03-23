package dev.simplix.cirrus.menu;

import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import dev.simplix.cirrus.service.MenuBuildService;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class represents a displayed menu.
 */
public final class DisplayedMenu {

    private final Menu value;
    private final Object nativeMenu;
    private final CirrusPlayerWrapper player;
    private final MenuBuildService menuBuildService;
    private final Long id;
    private final AtomicBoolean closed;

    /**
     * Creates a new instance of {@code DisplayedMenu}.
     *
     * @param value            the menu itself
     * @param nativeMenu       the native menu object
     * @param player           the player who sees the menu
     * @param menuBuildService the menu build service
     * @param id               the id of the menu
     * @param closed           whether the menu is closed
     */
    public DisplayedMenu(
        Menu value,
        Object nativeMenu,
        CirrusPlayerWrapper player,
        MenuBuildService menuBuildService,
        Long id,
        AtomicBoolean closed) {
        this.value = value;
        this.nativeMenu = nativeMenu;
        this.player = player;
        this.menuBuildService = menuBuildService;
        this.id = id;
        this.closed = closed;
    }

    /**
     * Creates a new instance of {@code DisplayedMenu}.
     *
     * @param value            the menu itself
     * @param nativeMenu       the native menu object
     * @param player           the player who sees the menu
     * @param menuBuildService the menu build service
     * @param id               the id of the menu
     */
    public DisplayedMenu(
        Menu value,
        Object nativeMenu,
        CirrusPlayerWrapper player,
        MenuBuildService menuBuildService,
        Long id) {
        this(value, nativeMenu, player, menuBuildService, id, new AtomicBoolean(false));
        Menus.register(player.uuid(), this);
    }

    public Menu value() {
        return this.value;
    }

    public Object nativeMenu() {
        return this.nativeMenu;
    }

    public CirrusPlayerWrapper player() {
        return this.player;
    }

    public MenuBuildService menuBuildService() {
        return this.menuBuildService;
    }

    public Long id() {
        return this.id;
    }

    public AtomicBoolean closed() {
        return this.closed;
    }

    /**
     * Indicates whether some other object is "equal to" this menu.
     *
     * @param other the reference object with which to compare
     * @return {@code true} if this menu is the same as the {@code other} argument; {@code false} otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }

        if (other instanceof DisplayedMenu) {
            DisplayedMenu otherMenu = (DisplayedMenu) other;
            return otherMenu.id.equals(this.id);
        }

        return false;
    }

    /**
     * Returns the hash code for this menu.
     *
     * @return the hash code for this menu
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, menuBuildService, id);
    }

    /**
     * Updates the menu.
     */
    public void update() {
        this.menuBuildService.updateMenu(this);
    }

    /**
     * Closes the menu.
     */
    public void close() {
        this.menuBuildService.closeMenu(this);
    }
}
