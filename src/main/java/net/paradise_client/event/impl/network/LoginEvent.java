package net.paradise_client.event.impl.network;

import com.mojang.authlib.GameProfile;

public record LoginEvent(GameProfile profile) {
  public LoginEvent() {
    this(null);
  }
}
