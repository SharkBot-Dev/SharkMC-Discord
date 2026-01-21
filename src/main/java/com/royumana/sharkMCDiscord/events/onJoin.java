package com.royumana.sharkMCDiscord.events;

import com.royumana.sharkMCDiscord.DatabaseManager;
import com.royumana.sharkMCDiscord.SharkMCDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;
import java.time.Instant;

public class onJoin implements Listener {
    private final SharkMCDiscord plugin;
    private final DatabaseManager databaseManager;

    public onJoin(SharkMCDiscord plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        String playerUuid = event.getPlayer().getUniqueId().toString();

        String targetChannelId = this.databaseManager.getChannelId();
        if (targetChannelId == null || targetChannelId.isEmpty()) return;

        if (plugin.jda == null) return;
        TextChannel textChannel = plugin.jda.getTextChannelById(targetChannelId);
        if (textChannel == null) return;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(
                playerName,
                null,
                "https://mc-heads.net/avatar/" + playerUuid
        );
        embed.setDescription(playerName + " さんがワールドに参加しました。");
        embed.setColor(new Color(0, 255, 255));
        embed.setTimestamp(Instant.now());

        textChannel.sendMessageEmbeds(embed.build()).queue();
    }
}