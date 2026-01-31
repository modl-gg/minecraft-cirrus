package dev.simplix.cirrus.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import dev.simplix.cirrus.menus.AbstractSearchableBrowser;
import dev.simplix.cirrus.model.SearchConversation;
import dev.simplix.cirrus.player.CirrusPlayerWrapper;
import dev.simplix.cirrus.service.SearchConversationHandleService;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.experimental.Accessors;

@SuppressWarnings({"UnstableApiUsage", "rawtypes", "unchecked"})
public abstract class AbstractSearchConversationHandlerService implements
    SearchConversationHandleService {

    protected static final Cache<UUID, Search> cache = CacheBuilder.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .removalListener((RemovalNotification<UUID, Search> notification) -> {
            final Search value = notification.getValue();
            if (value != null) {
                value.cirrusPlayerWrapper.sendMessage(value.searchConversation.timeoutMessage());
            }

        }).build();

    @Override
    public <T> void handle(
        CirrusPlayerWrapper playerWrapper,
        AbstractSearchableBrowser<T> browser,
        SearchConversation conversation) {

        final String prompt = conversation.prompt();
        playerWrapper.sendMessage(prompt);
    }

    protected boolean handleInput(
        CirrusPlayerWrapper playerWrapper,
        String playerMessage,
        AbstractSearchableBrowser browser) {

        final Search search = cache.getIfPresent(playerWrapper.uuid());
        if (search == null) {
            return false;
        }

        browser.redisplay(browser.searchByPartialString(playerMessage));
        return true;
    }

    @Data
    @Accessors(fluent = true)
    protected static class Search {

        private final CirrusPlayerWrapper cirrusPlayerWrapper;
        AbstractSearchableBrowser abstractSearchableBrowser;
        SearchConversation searchConversation;
    }
}
