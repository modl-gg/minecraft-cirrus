package dev.simplix.cirrus.menus;

import dev.simplix.cirrus.model.BusinessItemMap;
import dev.simplix.cirrus.model.CallResult;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import javax.annotation.Nullable;

/**
 * The BrowserSchematic is used to define the properties of a menu in the Cirrus framework. It
 * contains the following fields:
 * <ul>
 * <li>title: The title of the menu.
 * <li>standardResult: The default result of a click on an item in the menu.
 * <li>fixedSize: The fixed size of the menu, if it is not a standard inventory size.
 * <li>businessItemMap: A map of items that are used in the menu.
 * </ul>
 */
public final class BrowserSchematic {

    private final String title;
    @Nullable
    private final CallResult standardResult;
    @Nullable
    private final CirrusInventoryType fixedSize;
    @Nullable
    private final BusinessItemMap businessItemMap;

    public BrowserSchematic(
        String title,
        @Nullable CallResult standardResult,
        @Nullable CirrusInventoryType fixedSize,
        @Nullable BusinessItemMap businessItemMap) {
        this.title = title;
        this.standardResult = standardResult;
        this.fixedSize = fixedSize;
        this.businessItemMap = businessItemMap;
    }

    public String title() {
        return this.title;
    }

    @Nullable
    public CallResult standardResult() {
        return this.standardResult;
    }

    @Nullable
    public CirrusInventoryType fixedSize() {
        return this.fixedSize;
    }

    @Nullable
    public BusinessItemMap businessItemMap() {
        return this.businessItemMap;
    }
}
