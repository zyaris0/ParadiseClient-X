package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import io.github.spigotrce.paradiseclientfabric.inject.accessor.ClientPlayNetworkHandlerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

public class SayCommand extends Command {
    public SayCommand() {
        super("say", "Sends a chat message to the server");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.then(argument("message", StringArgumentType.greedyString())
                .executes(context -> {
                            String message = context.getArgument("message", String.class);
                            ((ClientPlayNetworkHandlerAccessor) MinecraftClient.getInstance().getNetworkHandler())
                                    .paradiseClient_Fabric$sendChatMessage(message);
                            return SINGLE_SUCCESS;
                        }
                )
        );
    }
}
