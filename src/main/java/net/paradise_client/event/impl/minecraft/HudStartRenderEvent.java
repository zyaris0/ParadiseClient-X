package net.paradise_client.event.impl.minecraft;

public record HudStartRenderEvent() {
  public static final HudStartRenderEvent INSTANCE = new HudStartRenderEvent();
}
