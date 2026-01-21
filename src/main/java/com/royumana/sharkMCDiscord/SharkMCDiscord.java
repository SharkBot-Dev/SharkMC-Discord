package com.royumana.sharkMCDiscord;

import com.royumana.sharkMCDiscord.events.onChat;
import com.royumana.sharkMCDiscord.events.onJoin;
import com.royumana.sharkMCDiscord.events.transferMessage;
import com.royumana.webDashboardPlugin.server.server;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;
import com.royumana.webDashboardPlugin.WebDashboardPlugin;
import com.royumana.webDashboardPlugin.lib.SidebarManager;

import java.sql.SQLException;

public final class SharkMCDiscord extends JavaPlugin {
    public JDA jda;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        DatabaseManager db = null;
        try {
            db = new DatabaseManager(getDataFolder() + "/discordbot.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        WebDashboardPlugin.getInstance().getHttpServer().addRouteExternal("/discord", DiscordSetting.class);
        WebDashboardPlugin.getInstance().getHttpServer().addRouteExternal("/api/discord/set-token", TokenUpdateHandler.class);
        SidebarManager.addMenu("DiscordBot", "/discord");

        String savedToken = null;
        try {
            savedToken = db.getToken();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (savedToken != null && !savedToken.isEmpty()) {
            startJDA(savedToken);
        }

        getServer().getPluginManager().registerEvents(new onChat(this, db), this);
        getServer().getPluginManager().registerEvents(new onJoin(this, db), this);
    }

    public void startJDA(String savedToken) {
        try {
            DatabaseManager db = new DatabaseManager(getDataFolder() + "/discordbot.db");

            try {
                this.jda = JDABuilder.createDefault(savedToken)
                        .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                        .addEventListeners(new transferMessage(this, db))
                        .build()
                        .awaitReady();
                getLogger().info("Discordボットが起動しました。");
            } catch (Exception e) {
                getLogger().severe("再起動に失敗しました: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void restartJDA(String newToken) {
        if (this.jda != null) {
            getLogger().info("既存のDiscord接続を終了しています...");
            this.jda.shutdownNow();
        }

        try {
            this.jda = JDABuilder.createDefault(newToken)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build()
                    .awaitReady();
            getLogger().info("Discordボットが起動しました。");
        } catch (Exception e) {
            getLogger().severe("再起動に失敗しました: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (this.jda != null) {
            this.jda.shutdownNow();
        }
    }
}
