package net.paradise_client.chatroom.common.model;

public record ServerModel(int port, boolean useHAProxy, int messageCooldown, int maxMessageCharacters, int connectionThrottle,
                          String hostname) {
}
