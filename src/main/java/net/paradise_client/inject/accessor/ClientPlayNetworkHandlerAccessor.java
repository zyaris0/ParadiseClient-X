package net.paradise_client.inject.accessor;

import net.paradise_client.event.impl.chat.*;

public interface ClientPlayNetworkHandlerAccessor {
  /**
   * Sends a chat message without firing the {@link ChatPostEvent} or {@link ChatPreEvent}.
   *
   * @param message The message to be sent.
   */
  void paradiseClient$sendChatMessage(String message);
}
