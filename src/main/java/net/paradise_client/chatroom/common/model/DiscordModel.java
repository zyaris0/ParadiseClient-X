package net.paradise_client.chatroom.common.model;

public record DiscordModel(String token, long serverID, boolean autoVerify, long verificationChannelID,
                           String webhookAccountLogging, long linkedMembersRoleID, long adminRoleID) {
}
