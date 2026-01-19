package com.royumana.sharkMCDiscord.events;

import com.royumana.sharkMCDiscord.DatabaseManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public class transferMessage extends ListenerAdapter {
    private final JavaPlugin plugin;
    private final DatabaseManager databaseManager;

    public transferMessage(JavaPlugin plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String targetChannelId = null;
        targetChannelId = databaseManager.getChannelId();
        // this.plugin.getLogger().info(targetChannelId);

        if (targetChannelId == null || !event.getChannel().getId().equals(targetChannelId)) {
            return;
        }

        String authorName = event.getAuthor().getName();
        String content = event.getMessage().getContentRaw();

        Bukkit.getScheduler().runTask(this.plugin, () -> {
            String formattedMessage = ChatColor.AQUA + "[" + authorName + "] " + ChatColor.WHITE + content;
            Bukkit.broadcastMessage(formattedMessage);
        });
    }
}