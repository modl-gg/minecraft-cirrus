package dev.simplix.cirrus.common.packet;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow.WindowClickType;
import dev.simplix.cirrus.model.CirrusClickType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ClickTypeMapper {

    public CirrusClickType mapClickType(WindowClickType type, int button, int slot) {
        switch (type) {
            case PICKUP:
                return button == 0 ? CirrusClickType.LEFT_CLICK : CirrusClickType.RIGHT_CLICK;
            case QUICK_MOVE:
                return button == 0 ? CirrusClickType.SHIFT_LEFT_CLICK : CirrusClickType.SHIFT_RIGHT_CLICK;
            case SWAP:
                switch (button) {
                    case 0: return CirrusClickType.NUMBER_KEY_1;
                    case 1: return CirrusClickType.NUMBER_KEY_2;
                    case 2: return CirrusClickType.NUMBER_KEY_3;
                    case 3: return CirrusClickType.NUMBER_KEY_4;
                    case 4: return CirrusClickType.NUMBER_KEY_5;
                    case 5: return CirrusClickType.NUMBER_KEY_6;
                    case 6: return CirrusClickType.NUMBER_KEY_7;
                    case 7: return CirrusClickType.NUMBER_KEY_8;
                    case 8: return CirrusClickType.NUMBER_KEY_9;
                    case 40: return CirrusClickType.OFFHAND_SWAP;
                    default: return CirrusClickType.UNKNOWN;
                }
            case CLONE:
                return CirrusClickType.MIDDLE_CLICK;
            case THROW:
                return button == 0 ? CirrusClickType.DROP : CirrusClickType.CTRL_DROP;
            case QUICK_CRAFT:
                return mapQuickCraft(button);
            case PICKUP_ALL:
                return CirrusClickType.DOUBLE_CLICK;
            default:
                return CirrusClickType.UNKNOWN;
        }
    }

    public CirrusClickType mapQuickCraft(int button) {
        switch (button) {
            case 0: return CirrusClickType.DRAG_START_LEFT;
            case 4: return CirrusClickType.DRAG_START_RIGHT;
            case 8: return CirrusClickType.DRAG_START_MIDDLE;
            case 1: return CirrusClickType.DRAG_ADD_LEFT;
            case 5: return CirrusClickType.DRAG_ADD_RIGHT;
            case 9: return CirrusClickType.DRAG_ADD_MIDDLE;
            case 2: return CirrusClickType.DRAG_END_LEFT;
            case 6: return CirrusClickType.DRAG_END_RIGHT;
            case 10: return CirrusClickType.DRAG_END_MIDDLE;
            default: return CirrusClickType.UNKNOWN;
        }
    }
}
