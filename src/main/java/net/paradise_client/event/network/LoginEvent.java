package net.paradise_client.event.network;

import com.mojang.authlib.GameProfile;
import io.github.spigotrce.eventbus.event.*;

public class LoginEvent extends Event implements Cancellable {
  private boolean disableHandler;
  private GameProfile profile;

  public LoginEvent(GameProfile profile) {
    this.disableHandler = false;
    this.profile = profile;
  }

  public GameProfile getProfile() {
    return profile;
  }

  public void setProfile(GameProfile profile) {
    this.profile = profile;
  }

  @Override public boolean isCancel() {
    return isDisableHandler();
  }

  public boolean isDisableHandler() {
    return disableHandler;
  }

  public void setDisableHandler(boolean disableHandler) {
    this.disableHandler = disableHandler;
  }

  @Override public void setCancel(boolean b) {
    setDisableHandler(b);
  }
}
