package com.royumana.sharkMCDiscord;

import java.sql.*;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        createTables();
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS config (key TEXT PRIMARY KEY, value TEXT)");
        }
    }

    private void setValue(String key, String value) throws SQLException {
        String sql = "INSERT OR REPLACE INTO config (key, value) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        }
    }

    private String getValue(String key) throws SQLException {
        String sql = "SELECT value FROM config WHERE key = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("value");
            }
        }
        return null;
    }

    public void saveToken(String token) throws SQLException {
        setValue("discord_token", token);
    }

    public String getToken() throws SQLException {
        return getValue("discord_token");
    }

    public void saveChannelId(String channelId) throws SQLException {
        setValue("channelId", channelId);
    }

    public String getChannelId() {
        try {
            return getValue("channelId");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}