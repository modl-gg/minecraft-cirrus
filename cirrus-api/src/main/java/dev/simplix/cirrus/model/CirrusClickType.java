package dev.simplix.cirrus.model;

public enum CirrusClickType {

    LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_RIGHT_CLICK,
    MIDDLE_CLICK,
    DROP,
    CTRL_DROP,
    DOUBLE_CLICK,
    NUMBER_KEY_1,
    NUMBER_KEY_2,
    NUMBER_KEY_3,
    NUMBER_KEY_4,
    NUMBER_KEY_5,
    NUMBER_KEY_6,
    NUMBER_KEY_7,
    NUMBER_KEY_8,
    NUMBER_KEY_9,
    OFFHAND_SWAP,
    DRAG_START_LEFT,
    DRAG_START_RIGHT,
    DRAG_START_MIDDLE,
    DRAG_ADD_LEFT,
    DRAG_ADD_RIGHT,
    DRAG_ADD_MIDDLE,
    DRAG_END_LEFT,
    DRAG_END_RIGHT,
    DRAG_END_MIDDLE,
    LEFT_CLICK_OUTSIDE,
    RIGHT_CLICK_OUTSIDE,
    CREATIVE,
    SWAP_OFFHAND,
    UNKNOWN;

    public boolean isLeftClick() {
        return this == LEFT_CLICK || this == SHIFT_LEFT_CLICK;
    }

    public boolean isRightClick() {
        return this == RIGHT_CLICK || this == SHIFT_RIGHT_CLICK;
    }

    public boolean isShiftClick() {
        return this == SHIFT_LEFT_CLICK || this == SHIFT_RIGHT_CLICK;
    }

    public boolean isNumberKey() {
        return this.name().startsWith("NUMBER_KEY_");
    }

    public boolean isDrag() {
        return this.name().startsWith("DRAG_");
    }

    public int getNumberKeySlot() {
        if (!isNumberKey()) return -1;
        return Integer.parseInt(this.name().substring("NUMBER_KEY_".length())) - 1;
    }
}
