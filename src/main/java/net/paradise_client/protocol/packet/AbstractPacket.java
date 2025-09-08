package net.paradise_client.protocol.packet;

import com.google.common.base.*;
import com.mojang.authlib.properties.Property;
import io.netty.buffer.*;
import net.paradise_client.protocol.*;
import se.llbit.nbt.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Minimal implementation of BungeeCord protocol.
 *
 * @author SpigotRCE
 */
public abstract class AbstractPacket {
  public static <T> T readStringMapKey(ByteBuf buf, Map<String, T> map) {
    String string = readString(buf);
    T result = (T) map.get(string);
    Preconditions.checkArgument(result != null, "Unknown string key %s", string);
    return result;
  }

  public static String readString(ByteBuf buf) {
    return readString(buf, 32767);
  }

  public static String readString(ByteBuf buf, int maxLen) {
    int len = readVarInt(buf);
    if (len > maxLen * 3) {
      throw new BadPacketException("Cannot receive string longer than " + maxLen * 3 + " (got " + len + " bytes)");
    } else {
      String s = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
      buf.readerIndex(buf.readerIndex() + len);
      if (s.length() > maxLen) {
        throw new BadPacketException("Cannot receive string longer than " +
          maxLen +
          " (got " +
          s.length() +
          " characters)");
      } else {
        return s;
      }
    }
  }

  public static int readVarInt(ByteBuf input) {
    return readVarInt(input, 5);
  }

  public static int readVarInt(ByteBuf input, int maxBytes) {
    int out = 0;
    int bytes = 0;

    byte in;
    do {
      in = input.readByte();
      out |= (in & 127) << bytes++ * 7;
      if (bytes > maxBytes) {
        throw new BadPacketException("VarInt too big (max " + maxBytes + ")");
      }
    } while ((in & 128) == 128);

    return out;
  }

  public static void writeArray(byte[] b, ByteBuf buf) {
    if (b.length > 32767) {
      throw new BadPacketException("Cannot send byte array longer than Short.MAX_VALUE (got " + b.length + " bytes)");
    } else {
      writeVarInt(b.length, buf);
      buf.writeBytes(b);
    }
  }

  public static void writeVarInt(int value, ByteBuf output) {
    do {
      int part = value & 127;
      value >>>= 7;
      if (value != 0) {
        part |= 128;
      }

      output.writeByte(part);
    } while (value != 0);

  }

  public static byte[] toArray(ByteBuf buf) {
    byte[] ret = new byte[buf.readableBytes()];
    buf.readBytes(ret);
    return ret;
  }

  public static byte[] readArray(ByteBuf buf) {
    return readArray(buf, buf.readableBytes());
  }

  public static byte[] readArray(ByteBuf buf, int limit) {
    int len = readVarInt(buf);
    if (len > limit) {
      throw new BadPacketException("Cannot receive byte array longer than " + limit + " (got " + len + " bytes)");
    } else {
      byte[] ret = new byte[len];
      buf.readBytes(ret);
      return ret;
    }
  }

  public static int[] readVarIntArray(ByteBuf buf) {
    int len = readVarInt(buf);
    int[] ret = new int[len];

    for (int i = 0; i < len; ++i) {
      ret[i] = readVarInt(buf);
    }

    return ret;
  }

  public static void writeStringArray(List<String> s, ByteBuf buf) {
    writeVarInt(s.size(), buf);

    for (String str : s) {
      writeString(str, buf);
    }

  }

  public static void writeString(String s, ByteBuf buf) {
    writeString(s, buf, 32767);
  }

  public static void writeString(String s, ByteBuf buf, int maxLength) {
    if (s.length() > maxLength) {
      throw new BadPacketException("Cannot send string longer than " +
        maxLength +
        " (got " +
        s.length() +
        " characters)");
    } else {
      byte[] b = s.getBytes(StandardCharsets.UTF_8);
      if (b.length > maxLength * 3) {
        throw new BadPacketException("Cannot send string longer than " +
          maxLength * 3 +
          " (got " +
          b.length +
          " bytes)");
      } else {
        writeVarInt(b.length, buf);
        buf.writeBytes(b);
      }
    }
  }

  public static List<String> readStringArray(ByteBuf buf) {
    int len = readVarInt(buf);
    List<String> ret = new ArrayList<>(len);

    for (int i = 0; i < len; ++i) {
      ret.add(readString(buf));
    }

    return ret;
  }

  public static void setVarInt(int value, ByteBuf output, int pos, int len) {
    switch (len) {
      case 1:
        output.setByte(pos, value);
        break;
      case 2:
        output.setShort(pos, (value & 127 | 128) << 8 | value >>> 7 & 127);
        break;
      case 3:
        output.setMedium(pos, (value & 127 | 128) << 16 | (value >>> 7 & 127 | 128) << 8 | value >>> 14 & 127);
        break;
      case 4:
        output.setInt(pos,
          (value & 127 | 128) << 24 |
            (value >>> 7 & 127 | 128) << 16 |
            (value >>> 14 & 127 | 128) << 8 |
            value >>> 21 & 127);
        break;
      case 5:
        output.setInt(pos,
          (value & 127 | 128) << 24 |
            (value >>> 7 & 127 | 128) << 16 |
            (value >>> 14 & 127 | 128) << 8 |
            value >>> 21 & 127 |
            128);
        output.setByte(pos + 4, value >>> 28);
        break;
      default:
        throw new IllegalArgumentException("Invalid varint len: " + len);
    }

  }

  public static int readVarShort(ByteBuf buf) {
    int low = buf.readUnsignedShort();
    int high = 0;
    if ((low & '耀') != 0) {
      low &= 32767;
      high = buf.readUnsignedByte();
    }

    return (high & 255) << 15 | low;
  }

  public static void writeVarShort(ByteBuf buf, int toWrite) {
    int low = toWrite & 32767;
    int high = (toWrite & 8355840) >> 15;
    if (high != 0) {
      low |= 32768;
    }

    buf.writeShort(low);
    if (high != 0) {
      buf.writeByte(high);
    }

  }

  public static void writeUUID(UUID value, ByteBuf output) {
    output.writeLong(value.getMostSignificantBits());
    output.writeLong(value.getLeastSignificantBits());
  }

  public static UUID readUUID(ByteBuf input) {
    return new UUID(input.readLong(), input.readLong());
  }

  public static void writeProperties(Property[] properties, ByteBuf buf) {
    if (properties == null) {
      writeVarInt(0, buf);
    } else {
      writeVarInt(properties.length, buf);

      for (Property prop : properties) {
        writeString(prop.name(), buf);
        writeString(prop.value(), buf);
        if (prop.signature() != null) {
          buf.writeBoolean(true);
          writeString(prop.signature(), buf);
        } else {
          buf.writeBoolean(false);
        }
      }

    }
  }

  public static Property[] readProperties(ByteBuf buf) {
    Property[] properties = new Property[readVarInt(buf)];

    for (int j = 0; j < properties.length; ++j) {
      String name = readString(buf);
      String value = readString(buf);
      if (buf.readBoolean()) {
        properties[j] = new Property(name, value, readString(buf));
      } else {
        properties[j] = new Property(name, value);
      }
    }

    return properties;
  }

  public static Tag readTag(ByteBuf input, int protocolVersion) {
    DataInputStream in = new DataInputStream(new ByteBufInputStream(input));
    Tag tag;
    if (protocolVersion >= 764) {
      try {
        byte type = in.readByte();
        if (type == 0) {
          return Tag.END;
        }

        tag = SpecificTag.read(type, in);
      } catch (IOException ex) {
        tag = new ErrorTag("IOException while reading tag type:\n" + ex.getMessage());
      }
    } else {
      tag = NamedTag.read(in);
    }

    Preconditions.checkArgument(!tag.isError(), "Error reading tag: %s", tag.error());
    return tag;
  }

  public static void writeTag(Tag tag, ByteBuf output, int protocolVersion) {
    DataOutputStream out = new DataOutputStream(new ByteBufOutputStream(output));

    try {
      if (tag instanceof SpecificTag specificTag) {
        specificTag.writeType(out);
        specificTag.write(out);
      } else {
        tag.write(out);
      }

    } catch (IOException ex) {
      throw new RuntimeException("Exception writing tag", ex);
    }
  }

  public static <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumset, Class<E> oclass, ByteBuf buf) {
    E[] enums = (E[]) (oclass.getEnumConstants());
    BitSet bits = new BitSet(enums.length);

    for (int i = 0; i < enums.length; ++i) {
      bits.set(i, enumset.contains(enums[i]));
    }

    writeFixedBitSet(bits, enums.length, buf);
  }

  public static void writeFixedBitSet(BitSet bits, int size, ByteBuf buf) {
    if (bits.length() > size) {
      throw new BadPacketException("BitSet too large (expected " + size + " got " + bits.size() + ")");
    } else {
      buf.writeBytes(Arrays.copyOf(bits.toByteArray(), size + 7 >> 3));
    }
  }

  public static <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> oclass, ByteBuf buf) {
    E[] enums = (E[]) (oclass.getEnumConstants());
    BitSet bits = readFixedBitSet(enums.length, buf);
    EnumSet<E> set = EnumSet.noneOf(oclass);

    for (int i = 0; i < enums.length; ++i) {
      if (bits.get(i)) {
        set.add(enums[i]);
      }
    }

    return set;
  }

  public static BitSet readFixedBitSet(int i, ByteBuf buf) {
    byte[] bits = new byte[i + 7 >> 3];
    buf.readBytes(bits);
    return BitSet.valueOf(bits);
  }

  public <T> T readNullable(Function<ByteBuf, T> reader, ByteBuf buf) {
    return (T) (buf.readBoolean() ? reader.apply(buf) : null);
  }

  public <T> void writeNullable(T t0, BiConsumer<T, ByteBuf> writer, ByteBuf buf) {
    if (t0 != null) {
      buf.writeBoolean(true);
      writer.accept(t0, buf);
    } else {
      buf.writeBoolean(false);
    }

  }

  public void read(ByteBuf buf, Protocol protocol, ProtocolVersion.Direction direction, int protocolVersion) {
    this.read(buf, direction, protocolVersion);
  }

  public void read(ByteBuf buf, ProtocolVersion.Direction direction, int protocolVersion) {
    this.read(buf);
  }

  public void read(ByteBuf buf) {
    throw new UnsupportedOperationException("Packet must implement read method");
  }

  public void write(ByteBuf buf, Protocol protocol, ProtocolVersion.Direction direction, int protocolVersion) {
    this.write(buf, direction, protocolVersion);
  }

  public void write(ByteBuf buf, ProtocolVersion.Direction direction, int protocolVersion) {
    this.write(buf);
  }

  public void write(ByteBuf buf) {
    throw new UnsupportedOperationException("Packet must implement write method");
  }

  public Protocol nextProtocol() {
    return null;
  }

  public abstract void handle(AbstractPacketHandler var1) throws Exception;

  public abstract int hashCode();

  public abstract boolean equals(Object var1);

  public abstract String toString();
}
