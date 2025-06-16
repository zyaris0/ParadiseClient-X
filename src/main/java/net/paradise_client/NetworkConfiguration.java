package net.paradise_client;

import net.minecraft.network.NetworkPhase;
import net.minecraft.network.NetworkSide;

public class NetworkConfiguration {
    public NetworkPhase phase;
    public NetworkSide side;
    public int protocolVersion;

    public void set(NetworkPhase phase, NetworkSide side, int protocolVersion) {
        this.phase = phase;
        this.side = side;
        this.protocolVersion = protocolVersion;
    }
}
