package dev.simplix.cirrus.item;

import dev.simplix.cirrus.color.StandardColorConfiguration;
import dev.simplix.cirrus.effects.SpectrumEffect;
import dev.simplix.cirrus.effects.WaveEffect;
import dev.simplix.cirrus.menus.AbstractBrowser;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.awt.Color;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Items {

    private static final CirrusItemType LIME_DYE = CirrusItemType.of("minecraft:lime_dye");
    private static final CirrusItemType GRAY_DYE = CirrusItemType.of("minecraft:gray_dye");

    public CirrusItem previousPageItem(AbstractBrowser<?> browser) {
        final int totalPages = browser.totalPages();
        final int previous = browser.previousPageNumber();

        if (browser.hasPreviousPage()) {
            return CirrusItem
                .of(
                    LIME_DYE,
                    CirrusChatElement.ofLegacyText("§aPrevious page"),
                    CirrusChatElement.ofLegacyText("§7Click to go to the previous page"),
                    CirrusChatElement.ofLegacyText("§7Goto page §8" + previous + " §7of §8" + totalPages)
                )
                .actionHandler(AbstractBrowser.PREVIOUS_PAGE_ACTION_HANDLER);
        } else {
            return CirrusItem.of(
                GRAY_DYE,
                CirrusChatElement.ofLegacyText("§aPrevious page"),
                CirrusChatElement.ofLegacyText("§7There is no previous page"));
        }
    }

    public CirrusItem nextPageItem(AbstractBrowser<?> browser) {
        final int totalPages = browser.totalPages();
        final int nextPageNumber = browser.nextPageNumber();

        if (browser.hasNextPage()) {
            return CirrusItem.of(
                LIME_DYE,
                CirrusChatElement.ofLegacyText("§aNext page"),
                CirrusChatElement.ofLegacyText("§7Click to go to the next page"),
                CirrusChatElement.ofLegacyText("§7Goto page §8" + nextPageNumber + " §7of §8" + totalPages)
            ).actionHandler(AbstractBrowser.NEXT_PAGE_ACTION_HANDLER);
        } else {
            return CirrusItem.of(
                GRAY_DYE,
                CirrusChatElement.ofLegacyText("§aNext page"),
                CirrusChatElement.ofLegacyText("§7There is no next page"));
        }
    }

    public CirrusItem withSpectrumEffect(CirrusItemType itemType, String name, CirrusChatElement... lores) {
        SpectrumEffect animation = SpectrumEffect.fat(
            name,
            StandardColorConfiguration.firstColor,
            StandardColorConfiguration.accentColor);
        return CirrusItem.of(itemType, animation, lores);
    }

    public CirrusItem withWaveEffect(CirrusItemType itemType, String name, CirrusChatElement... lores) {
        WaveEffect animation = WaveEffect
            .fat(name, Color.WHITE, StandardColorConfiguration.accentColor);
        return CirrusItem.of(itemType, animation, lores);
    }
}
