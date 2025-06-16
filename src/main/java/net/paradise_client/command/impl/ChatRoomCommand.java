package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.chatroom.client.Client;
import net.paradise_client.chatroom.client.TokenStore;
import net.paradise_client.chatroom.common.packet.PacketRegistry;
import net.paradise_client.chatroom.common.packet.impl.MessagePacket;
import net.paradise_client.command.Command;
import net.minecraft.command.CommandSource;

import java.io.IOException;

public class ChatRoomCommand extends Command {
    public ChatRoomCommand() {
        super("chatroom", "Connects you to a chatroom", true);
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
                    Helper.printChatMessage("Incomplete command!");
                    return SINGLE_SUCCESS;
                })
                .then(literal("say")
                        .executes(context -> {
                            Helper.printChatMessage("Incomplete command!");
                            return SINGLE_SUCCESS;
                        })
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    if (!ParadiseClient.CHAT_ROOM_MOD.isConnected) {
                                        Helper.printChatMessage("§4§lError: Not connected to chatroom");
                                        return SINGLE_SUCCESS;
                                    }

                                    String message = context.getArgument("message", String.class);
                                    PacketRegistry.sendPacket(
                                            new MessagePacket(message),
                                            ParadiseClient.CHAT_ROOM_MOD.channel
                                    );
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("token")
                        .executes(context -> {
                            Helper.printChatMessage("Incomplete command!");
                            return SINGLE_SUCCESS;
                        })
                        .then(argument("token", StringArgumentType.greedyString())
                                .executes(context -> {
                                    try {
                                        TokenStore.writeToken(context.getArgument("token", String.class));
                                    } catch (IOException e) {
                                        Helper.printChatMessage("§4§lError: Failed to write token");
                                        Constants.LOGGER.error("Failed to write token", e);
                                        return SINGLE_SUCCESS;
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("connect")
                        .executes(context -> {
                            if (ParadiseClient.CHAT_ROOM_MOD.isConnected) {
                                Helper.printChatMessage("You are already connected to chatroom");
                                return SINGLE_SUCCESS;
                            }
                            try {
                                Client.connect();
                            } catch (Exception e) {
                                Helper.printChatMessage("§4§lError: Failed to connect to chatroom");
                                Constants.LOGGER.error("Failed to connect to chatroom", e);
                            }
                            return SINGLE_SUCCESS;
                        })
                )
                .then(literal("disconnect")
                        .executes(context -> {
                            if (!ParadiseClient.CHAT_ROOM_MOD.isConnected) {
                                Helper.printChatMessage("§4§lError: Not connected to chatroom");
                                return SINGLE_SUCCESS;
                            }
                            Client.stop();
                            return SINGLE_SUCCESS;
                        })
                );
    }
}
