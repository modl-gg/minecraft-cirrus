package dev.simplix.cirrus.menus;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import dev.simplix.cirrus.Utils;
import dev.simplix.cirrus.actionhandler.ActionHandler;
import dev.simplix.cirrus.actionhandler.RegisteredActionHandler;
import dev.simplix.cirrus.item.CirrusItem;
import dev.simplix.cirrus.menu.DisplayedMenu;
import dev.simplix.cirrus.menu.Menu;
import dev.simplix.cirrus.menu.MenuElement;
import dev.simplix.cirrus.model.BusinessItemMap;
import dev.simplix.cirrus.model.CallResult;
import dev.simplix.cirrus.model.Click;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import dev.simplix.cirrus.menu.CirrusInventoryType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract implementation of a browser Represent a menu with a list of items that are browsable.
 * Menu is paginated and the size of the pages are automatically defined This behavior can be
 * overridden using {@link #fixedSize}
 *
 * @param <T> Type of the elements to be displayed
 */
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@Accessors(fluent = true, makeFinal = true)
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public abstract class AbstractBrowser<T> {

    /**
     * Defines the call result that should
     */
    protected CallResult standardResult = CallResult.DENY_GRABBING;
    //
    //
    //
    @Setter(AccessLevel.NONE)
    private transient final AtomicInteger currentPageIndex = new AtomicInteger(0);
    private transient final BiMap<String, T> mapped = HashBiMap.create();
    private final transient List<RegisteredActionHandler> actionHandlers = new LinkedList<>();
    /**
     * Map of slot index to static MenuElement. These slots will not contain browser items
     * and will remain static across all pages.
     */
    private final transient Map<Integer, MenuElement> interceptedSlots = new HashMap<>();
    private transient List<BrowserPage> pages = new ArrayList<>();
    private BusinessItemMap businessItemMap;
    @NonNull
    private String title;
    /**
     * If this is set to null, the fixedSize of the menu will be determined automatically based on the
     * amount of items the menu will contain. Must at least be capable of holding 18 items.
     */
    private CirrusInventoryType fixedSize = null;
    private boolean built = false;
    public static final String NEXT_PAGE_ACTION_HANDLER = "nextPage";
    public static final String PREVIOUS_PAGE_ACTION_HANDLER = "previousPage";

    //
    //
    //
    public static final String CLICK_ACTION_HANDLER = "Click_";

    public AbstractBrowser(@NonNull BrowserSchematic browserSchematic) {
        loadFrom(browserSchematic);
    }

    public void loadFrom(BrowserSchematic browserSchematic) {
        this.title = browserSchematic.title();
        if (browserSchematic.standardResult() != null) {
            this.standardResult = browserSchematic.standardResult();
        }
        if (browserSchematic.fixedSize() != null) {
            this.fixedSize = browserSchematic.fixedSize();
        }
        if (browserSchematic.businessItemMap() != null) {
            this.businessItemMap = browserSchematic.businessItemMap();
        }
    }

    // ----------------------------------------------------------------------------------------------------
    // API
    // ----------------------------------------------------------------------------------------------------

    public AbstractBrowser(List<BrowserPage> pages) {
        this.pages = pages;
    }

    public DisplayedMenu display(CirrusPlayerWrapper player) {
        if (!built) {
            build();
        }
        return currentPage().display(player);
    }

    public void registerActionHandler(String name, Consumer<Click> consumer) {
        actionHandlers.add(new RegisteredActionHandler(name, click -> {
            consumer.accept(click);
            return CallResult.DENY_GRABBING;
        }));
    }

    public void registerActionHandler(String name, ActionHandler actionHandler) {
        actionHandlers.add(new RegisteredActionHandler(name, actionHandler));
    }

    public int currentPageNumber() {
        return currentPageIndex.get() + 1;
    }

    public int totalPages() {
        return pages().size();
    }

    public int nextPageNumber() {
        return currentPageIndex.get() + 2;
    }

    public int previousPageNumber() {
        return currentPageIndex.get();
    }

    public boolean hasNextPage() {
        return currentPageIndex.get() < pages.size() - 1;
    }

    /**
     * Called before navigating to the next page. Return {@code true} to block the built-in navigation
     * (e.g., when data for the next page has not been loaded yet).
     */
    protected boolean interceptNextPage(Click click) { return false; }

    /**
     * Called before navigating to the previous page. Return {@code true} to block the built-in navigation.
     */
    protected boolean interceptPreviousPage(Click click) { return false; }

    /**
     * Sets the current page index without rebuilding the menu.
     * Useful for reopening a menu at a specific page after a rebuild.
     */
    protected void setInitialPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pages.size()) {
            currentPageIndex.set(pageIndex);
        }
    }

    // ----------------------------------------------------------------------------------------------------
    // Methods that might be overridden by subclasses
    // ----------------------------------------------------------------------------------------------------

    public boolean hasPreviousPage() {
        return currentPageIndex.get() > 0;
    }

    protected void registerActionHandlers() {

    }

    /**
     * See {@link Menu#updateTicks()}
     */
    protected int updateTicks() {
        return -1;
    }

    protected abstract void handleClick(Click click, T value);

    protected abstract Collection<T> elements();

    protected abstract CirrusItem map(T element);

    protected boolean addPageNumberToTitle() {
        return true;
    }

    protected String titleAddon(List<List<CirrusItem>> pages) {
        return " (" + currentPageNumber() + "/" + pages.size() + ")";
    }

    /**
     * Override this method to return a map of slots to intercept. Intercepted slots will remain static
     * across all pages and will not contain browser items. The key is the slot index, and the value
     * is the CirrusItem to display at that slot (or null for an empty slot).
     *
     * @param menuSize the total size of the menu (number of slots)
     * @return a map of slot index to CirrusItem (null values create empty intercepted slots)
     */
    protected Map<Integer, CirrusItem> intercept(int menuSize) {
        return Collections.emptyMap();
    }
    // ----------------------------------------------------------------------------------------------------
    // Implementation
    // ----------------------------------------------------------------------------------------------------

    private void build() {
        built = true;

        registerActionHandlers();

        currentPageIndex.set(0);
        if (elements() == null) {
            return;
        }

        // Determine menu size first
        int menuSize = fixedSize == null
                       ? Utils.calculateSizeForContent(elements().size())
                       : Utils.sizeOfType(fixedSize);

        // Get intercepted slots from subclass
        Map<Integer, CirrusItem> interceptMap = intercept(menuSize);
        for (Map.Entry<Integer, CirrusItem> entry : interceptMap.entrySet()) {
            MenuElement element = new MenuElement();
            if (entry.getValue() != null) {
                element.set(entry.getValue());
            }
            interceptedSlots.put(entry.getKey(), element);
        }

        // Calculate available slots (those not intercepted)
        List<Integer> availableSlots = IntStream.range(0, menuSize)
            .filter(slot -> !interceptedSlots.containsKey(slot))
            .boxed()
            .collect(Collectors.toList());

        int maximumItemsPerPage = availableSlots.size();

        final List<CirrusItem> collect = elements()
            .stream()
            .map(this::mapAndPut)
            .collect(Collectors.toList());

        if (maximumItemsPerPage == 0 || collect.isEmpty()) {
            currentPage().type(CirrusInventoryType.GENERIC_9X2);
            currentPage().title(this.title() + (addPageNumberToTitle() ? " (0/0)" : ""));
            return;
        }

        List<List<CirrusItem>> pages = Lists.partition(collect, maximumItemsPerPage);

        // Reset page index since we used currentPage() above
        this.pages.clear();
        currentPageIndex.set(0);

        for (List<CirrusItem> page : pages) {
            Menu menu = currentPage();
            menu.type(fixedSize == null ? Utils.calculateTypeForContent(menuSize) : fixedSize);

            // Place browser items only in available (non-intercepted) slots
            for (int i = 0; i < page.size(); i++) {
                CirrusItem cirrusItem = page.get(i);
                int actualSlot = availableSlots.get(i);
                cirrusItem.slot(actualSlot);
                cirrusItem.actionHandler(CLICK_ACTION_HANDLER);
                menu.set(cirrusItem);
            }

            final String stringToAdd = titleAddon(pages);
            menu.title(this.title() + (addPageNumberToTitle() ? stringToAdd : ""));

            currentPageIndex.incrementAndGet();
        }

        currentPageIndex.set(0);
    }

    private BrowserPage currentPage() {
        int index = Math.max(currentPageIndex.get(), 0);
        BrowserPage got = pages.size() > index ? pages.get(index) : null;
        if (got == null) {
            got = new BrowserPage();
            registerActionHandlersForMenu(got); // Initialize action handlers
            pages.add(got);
        }
        return got;
    }

    private CirrusItem mapAndPut(T element) {
        final CirrusItem result = map(element);
        final String toString = element.toString();
        mapped.put(toString, element);
        result.actionArguments(Collections.singletonList(toString));
        return result;
    }

    private void registerActionHandlersForMenu(Menu menu) {
        menu.registerActionHandler(CLICK_ACTION_HANDLER, (click) -> {
            final T t = mapped.get(click.arguments().get(0));
            if (t != null) {
                this.handleClick(click, t);
            }
        });
        menu.registerActionHandler(NEXT_PAGE_ACTION_HANDLER, (ActionHandler) (click) -> {
            if (interceptNextPage(click)) return CallResult.DENY_GRABBING;
            if (hasNextPage()) {
                currentPageIndex.incrementAndGet();
                currentPage().display(click.player());
            }
            return CallResult.DENY_GRABBING;
        });

        menu.registerActionHandler(PREVIOUS_PAGE_ACTION_HANDLER, (ActionHandler) (click) -> {
            if (interceptPreviousPage(click)) return CallResult.DENY_GRABBING;
            if (hasPreviousPage()) {
                currentPageIndex.decrementAndGet();
                currentPage().display(click.player());
            }
            return CallResult.DENY_GRABBING;
        });

        for (RegisteredActionHandler actionHandler : actionHandlers()) {
            menu.registerActionHandler(actionHandler.name(), actionHandler.handler());
        }
    }

    @RequiredArgsConstructor
    private class BrowserPage extends SimpleMenu {

        @Override
        protected void handleDisplay0() {
            // Render all intercepted slots (static items that remain across pages)
            for (Map.Entry<Integer, MenuElement> entry : interceptedSlots.entrySet()) {
                int slot = entry.getKey();
                MenuElement menuElement = entry.getValue();
                menuElement
                    .item()
                    .ifPresent(baseItemStack -> menuElement.applyChanges(this, slot));
            }
        }

        @Override
        public int updateTicks() {
            return AbstractBrowser.this.updateTicks();
        }
    }

}


