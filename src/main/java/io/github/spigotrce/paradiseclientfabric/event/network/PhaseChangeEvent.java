package io.github.spigotrce.paradiseclientfabric.event.network;

import io.github.spigotrce.eventbus.event.Event;
import net.minecraft.network.NetworkPhase;

/**
 * This event is fired when the network phase
 * is changed.
 */
public class PhaseChangeEvent extends Event {
    public final NetworkPhase phase;

    public PhaseChangeEvent(NetworkPhase phase) {
        this.phase = phase;
    }

    public NetworkPhase getPhase() {
        return phase;
    }
}
