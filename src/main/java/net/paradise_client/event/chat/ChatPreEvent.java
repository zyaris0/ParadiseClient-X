package net.paradise_client.event.chat;

import io.github.spigotrce.eventbus.event.*;

/**
 * Event before the chat message has been sent.
 */
@SuppressWarnings("unused") public class ChatPreEvent extends Event implements Cancellable {
  private boolean isCancel = false;
  private String message;

  public ChatPreEvent(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override public boolean isCancel() {
    return isCancel;
  }

  @Override public void setCancel(boolean b) {
    isCancel = b;
  }
}
