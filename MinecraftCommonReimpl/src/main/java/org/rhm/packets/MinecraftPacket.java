package org.rhm.packets;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.rhm.Identifier;
import org.rhm.MinecraftTypes;
import org.rhm.utils.CompressionUtils;
import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.BitSet;
import java.util.UUID;

public abstract class MinecraftPacket implements MinecraftTypes {
    public static final int maxPacketSize = 2097151;
    private final byte packetId;
    protected int dataSizeRemaining;
    protected int fullDataSize;

    public MinecraftPacket(int packetId) {
        this.packetId = (byte) packetId;
    }

    @Override
    public final Tag<?> readChat(DataInputStream inp, int size) throws IOException {
        Tag<?> val = MinecraftTypes.super.readChat(inp, size);
        dataSizeRemaining -= calculateChatSize(val) + 1;
        return val;
    }

    public final void writeToStream(MinecraftOutputStream output) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(maxPacketSize);
        MinecraftOutputStream dataOutputStream = new MinecraftOutputStream(byteArrayOutputStream);

        byte[] packet;
        int len;
        if (CompressionUtils.compressionThreshold < 0) {
            dataOutputStream.writeVarInt(packetId);
            write(dataOutputStream);
            dataOutputStream.flush();
            packet = byteArrayOutputStream.toByteArray();

            len = packet.length;
        } else {
            dataOutputStream.writeVarInt(packetId);
            write(dataOutputStream);
            dataOutputStream.flush();
            packet = byteArrayOutputStream.toByteArray();

            if (packet.length < CompressionUtils.compressionThreshold) {
                output.writeVarInt(packet.length + 1);
                len = 0;
            } else {
                int length = packet.length;
                packet = CompressionUtils.compress(packet);

                output.writeVarInt(length + packet.length);
                len = length;
            }
        }
        output.writeVarInt(len);
        output.write(packet);

        Logger.info("Sent C2S packet with id " + packetId + " (" + this.getClass().getSimpleName() + ") and length " + len);
        Logger.debug(Arrays.toString(Utils.getFirstNElements(packet, 100)));
    }

    public final void readFromStream(DataInputStream input, int packetSize) throws IOException {
        dataSizeRemaining = fullDataSize = packetSize;
        read(input);
        if (dataSizeRemaining != 0) {
            Logger.warn("Read invalid packet, remaining data is not 0, it is instead: " + dataSizeRemaining);
            if (dataSizeRemaining > 0) {
                //input.readNBytes(dataSizeRemaining);
                byte[] remainingData = new byte[dataSizeRemaining];
                input.readFully(remainingData);
                Logger.debug("Remaining data: " + Arrays.toString(remainingData));
            }
            //System.exit(0);
        }
    }

    protected abstract void read(DataInputStream in) throws IOException;

    protected abstract void write(MinecraftOutputStream out) throws IOException;

    @Override
    public final CompoundTag readNBTCompound(DataInputStream inp, int size) throws IOException {
        CompoundTag val = MinecraftTypes.super.readNBTCompound(inp, size);
        dataSizeRemaining -= calculateNBTCompoundSize(val) + 1;
        return val;
    }

    @Override
    public final int readVarInt(DataInputStream in) throws IOException {
        int n = MinecraftTypes.super.readVarInt(in);
        dataSizeRemaining -= calculateVarIntSize(n);
        return n;
    }

    @Override
    public final BitSet readBitset(DataInputStream inp) throws IOException {
        BitSet val = MinecraftTypes.super.readBitset(inp);
        dataSizeRemaining -= calculateBitsetSize(val);
        return val;
    }

    @Override
    public final UUID readUUID(DataInputStream inp) throws IOException {
        UUID val = MinecraftTypes.super.readUUID(inp);
        dataSizeRemaining -= calculateUUIDSize(val);
        return val;
    }

    @Override
    public final long readUnsignedLong(DataInputStream inp) throws IOException {
        long val = MinecraftTypes.super.readUnsignedLong(inp);
        dataSizeRemaining -= calculateUnsignedLongSize(val);
        return val;
    }

    @Override
    public final String readString(DataInputStream inp, Charset charset, int size) throws IOException {
        String val = MinecraftTypes.super.readString(inp, charset, size);
        dataSizeRemaining -= calculateStringSize(val, charset);
        return val;
    }

    public final byte[] readNBytes(DataInputStream inp, int n) throws IOException {
        dataSizeRemaining -= n;
        return inp.readNBytes(n);
    }

    public final boolean readBoolean(DataInputStream inp) throws IOException {
        dataSizeRemaining -= 1;
        return inp.readBoolean();
    }

    public final long readLong(DataInputStream inp) throws IOException {
        dataSizeRemaining -= Long.BYTES;
        return inp.readLong();
    }

    public final int readInt(DataInputStream inp) throws IOException {
        dataSizeRemaining -= Integer.BYTES;
        return inp.readInt();
    }

    public final double readDouble(DataInputStream inp) throws IOException {
        dataSizeRemaining -= Double.BYTES;
        return inp.readDouble();
    }

    public final float readFloat(DataInputStream inp) throws IOException {
        dataSizeRemaining -= Float.BYTES;
        return inp.readFloat();
    }

    public final int readUnsignedByte(DataInputStream inp) throws IOException {
        int val = inp.readUnsignedByte();
        dataSizeRemaining -= 1;
        return val;
    }

    public final float readAngle(DataInputStream inp) throws IOException {
        return readUnsignedByte(inp) / (256.0F / 360.0F);
    }

    public final short readShort(DataInputStream inp) throws IOException {
        short val = inp.readShort();
        dataSizeRemaining -= Short.BYTES;
        return val;
    }

    @Override
    public final Identifier readIdentifier(DataInputStream inp) throws IOException {
        Identifier val = MinecraftTypes.super.readIdentifier(inp);
        dataSizeRemaining -= calculateIdentifierSize(val);
        return val;
    }
}
