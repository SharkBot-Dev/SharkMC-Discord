package com.royumana.sharkMCDiscord.plg_events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public class DiscordMessageEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final User author;
    private final Message message;
    private final String content;

    public DiscordMessageEvent(User author, Message message) {
        this.author = author;
        this.message = message;
        this.content = message.getContentRaw();
    }

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}