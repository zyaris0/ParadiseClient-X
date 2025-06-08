package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.ParadiseClient_Fabric;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

/**
 * Represents a command that toggles the display of server IP on the HUD.
 *
 * @author SpigotRCE
 * @since 1.4
 */
public class ToggleTABCommand extends Command {

    /**
     * Constructs a new instance of {@link ToggleTABCommand}.
     */
    public ToggleTABCommand() {
        super("toggletab", "Toggles IP displayed on HUD");
    }

    /**
     * Builds the command structure using Brigadier's {@link LiteralArgumentBuilder}.
     */
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes((context -> {
            ParadiseClient_Fabric.HUD_MOD.showPlayerList = !ParadiseClient_Fabric.HUD_MOD.showPlayerList;
            Helper.printChatMessage(ParadiseClient_Fabric.HUD_MOD.showPlayerList ? "TAB shown" : "TAB hidden");
            return SINGLE_SUCCESS;
        }));
    }
}
