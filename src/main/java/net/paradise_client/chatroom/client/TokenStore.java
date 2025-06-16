package net.paradise_client.chatroom.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TokenStore {
    private static final Path FILE_PATH = Path.of("token.txt");
    public static String token;

    static {
        try {
            token = readToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readToken() throws IOException {
        if (!Files.exists(FILE_PATH)) {
            throw new NoSuchFileException("Token file not found.");
        }
        return Files.readString(FILE_PATH);
    }

    public static void writeToken(String token) throws IOException {
        try {
            Files.writeString(FILE_PATH, token, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Failed to write token.", e);
        }
    }

    public static void createFile() throws IOException {
        if (!Files.exists(FILE_PATH)) {
            try {
                Files.createFile(FILE_PATH);
            } catch (IOException e) {
                throw new IOException("Failed to create token file.", e);
            }
        }
    }
}

