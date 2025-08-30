package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.paradise_client.*;
import net.paradise_client.command.Command;

/**
 * Represents a command that displays help information for other commands.
 *
 * @author SpigotRCE
 * @since 1.4
 */
public class HelpCommand extends Command {

  /**
   * Constructs a new instance of {@link HelpCommand}.
   */
  public HelpCommand() {
    super("help", "Shows help page");
  }

  /**
   * Builds the command structure using Brigadier's {@link LiteralArgumentBuilder}.
   */
  @Override public void build(LiteralArgumentBuilder<CommandSource> root) {
    root.executes(context -> {
      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      Helper.printChatMessage("&b&l[Command List]");
      for (Command command : ParadiseClient.COMMAND_MANAGER.getCommands()) {
        Helper.printChatMessage("&7 - &a" + command.getName() + " &8| &f" + command.getDescription());
      }
      Helper.printChatMessage("&7 - &fTotal Registered: &b" + ParadiseClient.COMMAND_MANAGER.getCommands().size());
      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      return SINGLE_SUCCESS;
    }).then(argument("command", StringArgumentType.word()).executes(context -> {
      String name = context.getArgument("command", String.class);
      Command command = ParadiseClient.COMMAND_MANAGER.getCommand(name);

      if (command == null) {
        Helper.printChatMessage("&4&l[Error] &cCommand not found: &f" + name);
        return SINGLE_SUCCESS;
      }

      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      Helper.printChatMessage("&b&l[Command Info]");
      Helper.printChatMessage("&7 - &aName: &f" + command.getName());
      Helper.printChatMessage("&7 - &aDescription: &f" + command.getDescription());
      Helper.printChatMessage("&8&m-----------------------------------------------------", false);
      return SINGLE_SUCCESS;
    }).suggests((context, builder) -> {
      String partialName;
      try {
        partialName = context.getArgument("command", String.class);
      } catch (IllegalArgumentException e) {
        partialName = "";
      }

      String finalPartialName = partialName;
      ParadiseClient.COMMAND_MANAGER.getCommands()
        .stream()
        .map(Command::getName)
        .filter(cmd -> cmd.toLowerCase().startsWith(finalPartialName.toLowerCase()))
        .forEach(builder::suggest);

      return builder.buildFuture();
    }));
  }
}
