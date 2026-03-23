package dev.simplix.cirrus;

import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.item.CirrusItemType;
import dev.simplix.cirrus.menus.AbstractBrowser;
import dev.simplix.cirrus.model.Click;
import dev.simplix.cirrus.text.CirrusChatElement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CirrusTestExtension.class)
public class AbstractBrowserTest {

    private static AbstractBrowser<String> testBrowser;

    @BeforeAll
    public static void testBuilding() {

        testBrowser = new AbstractBrowser<String>() {

            @Override
            protected void handleClick(Click click, String value) {

            }

            @Override
            protected Collection<String> elements() {
                LinkedList<String> list = new LinkedList<>();

                for (int i = 0; i < 200; i++) {
                    list.add(Integer.toString(i));
                }

                return list;
            }

            @Override
            protected CirrusItem map(String element) {
                return CirrusItem.of(CirrusItemType.STONE, CirrusChatElement.ofLegacyText(element), CirrusChatElement.ofLegacyText("this is a test"));
            }

            @Override
            protected Map<Integer, CirrusItem> intercept(int menuSize) {
                int bottomRowStart = menuSize - 9;
                return Collections.singletonMap(
                    bottomRowStart,
                    CirrusItem.of(CirrusItemType.ACACIA_BOAT, CirrusChatElement.ofLegacyText("next page"))
                );
            }
        };

    }

    @Test
    public void testBuild() {
        //    testBrowser.build()
    }
}
