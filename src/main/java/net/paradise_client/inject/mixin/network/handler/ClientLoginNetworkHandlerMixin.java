package net.paradise_client.inject.mixin.network.handler;

import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.paradise_client.event.bus.EventBus;
import net.paradise_client.event.impl.network.LoginEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLoginNetworkHandler.class) public class ClientLoginNetworkHandlerMixin {
  @Inject(method = "onSuccess", at = @At("HEAD"), cancellable = true)
  public void onSucess(LoginSuccessS2CPacket packet, CallbackInfo ci) {
    EventBus.fire(EventBus.LOGIN_EVENT_EVENT_CHANNEL, new LoginEvent(packet.profile()));
  }
}
