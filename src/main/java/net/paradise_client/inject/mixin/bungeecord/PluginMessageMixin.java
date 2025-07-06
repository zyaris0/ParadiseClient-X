package net.paradise_client.inject.mixin.bungeecord;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.md_5.bungee.protocol.DefinedPacket.readString;
import static net.md_5.bungee.protocol.packet.PluginMessage.MODERNISE;

@Mixin(value = PluginMessage.class, remap = false)
public class PluginMessageMixin {
    @Shadow private String tag;

    @Shadow private byte[] data;

    /**
     * @author SpigotRCE
     * @reason To change the length of the tag
     */
    @Overwrite
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
        this.tag = protocolVersion >= 393 ? MODERNISE.apply(readString(buf)) : readString(buf, 64);
        int maxSize = direction == ProtocolConstants.Direction.TO_SERVER ? 32767 : 1048576;
        Preconditions.checkArgument(buf.readableBytes() <= maxSize, "Payload too large");
        data = new byte[buf.readableBytes()];
        buf.readBytes(data);
    }
}
