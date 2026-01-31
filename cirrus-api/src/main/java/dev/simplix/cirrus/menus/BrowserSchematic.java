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
public record BrowserSchematic(
    String title,
    @Nullable CallResult standardResult,
    @Nullable CirrusInventoryType fixedSize,
    @Nullable BusinessItemMap businessItemMap
) {

}
