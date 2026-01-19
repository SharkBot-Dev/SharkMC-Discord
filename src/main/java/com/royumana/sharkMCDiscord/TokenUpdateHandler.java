package com.royumana.sharkMCDiscord;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.royumana.webDashboardPlugin.server.isAuth.isLogined;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class TokenUpdateHandler extends RouterNanoHTTPD.GeneralHandler {
    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        if (!isLogined(session)) return newFixedLengthResponse(NanoHTTPD.Response.Status.UNAUTHORIZED, "text/plain", "Unauthorized");

        Map<String, String> files = new HashMap<>();
        try {
            session.parseBody(files);

            String newToken = session.getParms().get("token");
            String channelId = session.getParms().get("channelId");

            if (newToken != null && !newToken.isEmpty()) {
                SharkMCDiscord plugin = (SharkMCDiscord) Bukkit.getPluginManager().getPlugin("SharkMC-Discord");

                DatabaseManager db = null;
                try {
                    db = new DatabaseManager(plugin.getDataFolder() + "/discordbot.db");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                db.saveToken(newToken);
                db.saveChannelId(channelId);
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> plugin.restartJDA(newToken));

                if (!isLogined(session)) {
                    NanoHTTPD.Response res = newFixedLengthResponse(NanoHTTPD.Response.Status.REDIRECT, "text/plain", "");
                    res.addHeader("Location", "/discord");
                    return res;
                }
            }
        } catch (Exception e) {
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "ERROR: " + e.getMessage());
        }
        return newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/plain", "Missing token parameter.");
    }
}