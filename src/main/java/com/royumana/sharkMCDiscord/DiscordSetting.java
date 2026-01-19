package com.royumana.sharkMCDiscord;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.royumana.webDashboardPlugin.server.isAuth.isLogined;
import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

public class DiscordSetting extends RouterNanoHTTPD.GeneralHandler {

    private final SharkMCDiscord plugin = JavaPlugin.getPlugin(SharkMCDiscord.class);

    @Override
    public String getMimeType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {

        if (!isLogined(session)) {
            NanoHTTPD.Response res = newFixedLengthResponse(NanoHTTPD.Response.Status.REDIRECT, "text/plain", "");
            res.addHeader("Location", "/login");
            return res;
        }

        String html;
        try (InputStream in = plugin.getResource("web/discord/setting.html")) {
            if (in == null) {
                return newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/plain", "HTML file not found");
            }
            html = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load setting.html: " + e.getMessage());
            return newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/plain", "Internal Server Error");
        }

        return newFixedLengthResponse(
                getStatus(),
                getMimeType(),
                html
        );
    }
}