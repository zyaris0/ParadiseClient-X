package net.paradise_client.inject.accessor;

import net.paradise_client.event.chat.ChatPostEvent;
import net.paradise_client.event.chat.ChatPreEvent;

public interface ClientPlayNetworkHandlerAccessor {
    /**
     * Sends a chat message without firing the {@link ChatPostEvent} or {@link ChatPreEvent}.
     *
     * @param message The message to be sent.
     */
    void paradiseClient_Fabric$sendChatMessage(String message);
}
