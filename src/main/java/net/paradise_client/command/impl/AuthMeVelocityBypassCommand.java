package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.paradise_client.Helper;
import net.paradise_client.command.Command;
import net.paradise_client.packet.AuthMeVelocityPayloadPacket;

public class AuthMeVelocityBypassCommand extends Command {
    public AuthMeVelocityBypassCommand() {
        super("authmevelocitybypass", "Bypasses AuthMeVelocity");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            Helper.sendPacket(new CustomPayloadC2SPacket(
                    new AuthMeVelocityPayloadPacket()
            ));
            Helper.printChatMessage("Payload packet sent!");
            return Command.SINGLE_SUCCESS;
        });
    }
}
