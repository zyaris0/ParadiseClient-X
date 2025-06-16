package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
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
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes((context -> {
                    printDash();
                    for (Command command : ParadiseClient.COMMAND_MANAGER.getCommands()) {
                        Helper.printChatMessage("§a" + command.getName() + " §b" + command.getDescription(), false);
                    }
                    printDash();
                    Helper.printChatMessage("§aThere are currently §b" +
                                    ParadiseClient.COMMAND_MANAGER.getCommands().size() +
                                    "§a registered commands.",
                            false
                    );
                    printDash();
                    return SINGLE_SUCCESS;
                }))
                .then(argument("command", StringArgumentType.word())
                        .executes(context -> {
                            String name = context.getArgument("command", String.class);
                            Command command = ParadiseClient.COMMAND_MANAGER.getCommand(name);

                            if (command == null) {
                                Helper.printChatMessage("§4Command not found!");
                                return SINGLE_SUCCESS;
                            }

                            printDash();
                            Helper.printChatMessage("§a" + command.getName() + " §b" + command.getDescription(), false);
                            printDash();
                            return SINGLE_SUCCESS;
                        })
                        .suggests((context, builder) -> {
                            String partialName;
                            try {
                                partialName = context.getArgument("command", String.class);
                            } catch (IllegalArgumentException e) {
                                partialName = "";
                            }

                            if (partialName.isEmpty()) {
                                ParadiseClient.COMMAND_MANAGER.getCommands()
                                        .stream()
                                        .map(Command::getName)
                                        .forEach(builder::suggest);
                                return builder.buildFuture();
                            }

                            String finalPartialName = partialName;
                            ParadiseClient.COMMAND_MANAGER.getCommands()
                                    .stream()
                                    .map(Command::getName)
                                    .filter(name -> name.toLowerCase().startsWith(finalPartialName))
                                    .forEach(builder::suggest);

                            return builder.buildFuture();

                        })
                );
    }

    private void printDash() {
        double count = MinecraftClient.getInstance().options.getChatWidth().getValue() * 360 / (MinecraftClient.getInstance().textRenderer.getWidth("-") + 1);
        Helper.printChatMessage("§a" + "-".repeat(((int) count)), false);
    }
}
