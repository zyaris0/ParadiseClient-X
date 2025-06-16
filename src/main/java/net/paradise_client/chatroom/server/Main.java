package net.paradise_client.chatroom.server;

import net.paradise_client.chatroom.common.model.UserModel;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.server.config.Config;
import net.paradise_client.chatroom.server.database.MySQLDatabase;
import net.paradise_client.chatroom.server.discord.DiscordBotImpl;
import net.paradise_client.chatroom.server.exception.UserAlreadyRegisteredException;
import net.paradise_client.chatroom.server.exception.UserAlreadyVerifiedException;
import net.paradise_client.chatroom.server.netty.ChatRoomServer;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main {
    public static Config CONFIG = new Config(new File(System.getProperty("user.dir")).toPath());
    public static MySQLDatabase DATABASE;
    public static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static void main(String[] args) {
        try {
            CONFIG.load();
        } catch (IOException exception) {
            Logging.error("Unable to load configuration", exception);
            System.exit(1);
        }

        try {
            DATABASE = new MySQLDatabase();
        } catch (SQLException e) {
            Logging.error("Unable to connect to the database", e);
            System.exit(1);
        }

        DiscordBotImpl.startDiscordBot();
        PacketRegistry.registerPackets();

        try {
            ChatRoomServer.startServer(CONFIG.getServer());
        } catch (Exception exception) {
            Logging.error("Error starting chat server...", exception);
            System.exit(1);
        }
    }

    public static boolean registerNewUser(UserModel user) throws SQLException, UserAlreadyRegisteredException {
        DATABASE.insertUser(user.withVerified(CONFIG.getDiscord().autoVerify()));
        return CONFIG.getDiscord().autoVerify();
    }

    public static void verifyUser(UUID uuid) throws SQLException, UserAlreadyVerifiedException {
        UserModel model = DATABASE.getUser(uuid);
        if (model.verified()) throw new UserAlreadyVerifiedException(uuid);
        DATABASE.updateUser(DATABASE.getUser(uuid).withVerified(true));
    }

    public static boolean authenticate(String key) throws SQLException {
        List<String> split = Arrays.asList(key.split("\\."));
        UUID uuid = UUID.fromString(split.get(0));
        String token = split.get(1);
        UserModel model = DATABASE.getUser(uuid);
        return model.token().equals(token);
    }

    public static UserModel generateToken(UserModel model) throws SQLException {
        UserModel newModel = model.withToken(generateNextString(64));
        DATABASE.updateUser(newModel);
        return newModel;
    }

    private static String generateNextString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        return sb.toString();
    }
}
