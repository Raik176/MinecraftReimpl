package org.rhm;

import net.querz.nbt.io.NBTInputStream;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.UUID;

public interface MinecraftTypes {

    default Tag<?> readChat(DataInputStream inp, int size) throws IOException {
        byte[] bytes = inp.readNBytes(size);

        NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(bytes));
        try {
            return nbtInputStream.readRawTag(999);
        } catch (IOException e) {
            return new StringTag(nbtInputStream.readUTF());
        }
    }

    default int calculateChatSize(Tag<?> value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeChat(value);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate Chat Component size:");
            return -1;
        }
    }

    default int readVarInt(DataInputStream inp) throws IOException {
        return readVarIntInternal(inp);
    }

    private int readVarIntInternal(DataInputStream inp) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = inp.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    default int calculateVarIntSize(int value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeVarInt(value);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate VarInt size:");
            return -1;
        }
    }

    default BitSet readBitset(DataInputStream inp) throws IOException {
        int length = readVarIntInternal(inp);
        long[] longArray = new long[length];

        for (int i = 0; i < length; i++) {
            longArray[i] = inp.readLong();
        }

        BitSet bitSet = new BitSet(length * 64);

        for (int i = 0; i < length * 64; i++) {
            if ((longArray[i / 64] & (1L << (i % 64))) != 0) {
                bitSet.set(i);
            }
        }

        return bitSet;
    }

    default int calculateBitsetSize(BitSet value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeBitset(value);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate Bitset size:");
            return -1;
        }
    }

    default UUID readUUID(DataInputStream inp) throws IOException {
        long mostSigBits = readUnsignedLongInternal(inp);
        long leastSigBits = readUnsignedLongInternal(inp);

        return new UUID(mostSigBits, leastSigBits);
    }

    default int calculateUUIDSize(UUID value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeUUID(value);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate UUID size:");
            return -1;
        }
    }

    default long readUnsignedLong(DataInputStream inp) throws IOException {
        return readUnsignedLongInternal(inp);
    }

    private long readUnsignedLongInternal(DataInputStream inp) throws IOException {
        long signedLong = inp.readLong();
        return signedLong & 0xFFFFFFFFFFFFFFFFL;
    }

    default int calculateUnsignedLongSize(long value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeUnsignedLong(value);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate unsigned Long size:");
            return -1;
        }
    }

    default String readString(DataInputStream inp) throws IOException {
        return readString(inp, 32767);
    }

    default String readString(DataInputStream inp, int size) throws IOException {
        return readString(inp, StandardCharsets.UTF_8, size);
    }

    default String readString(DataInputStream inp, Charset charset) throws IOException {
        return readString(inp, charset, 32767);
    }

    default String readString(DataInputStream inp, Charset charset, int size) throws IOException {
        return readStringInternal(inp, charset, size);
    }

    private String readStringInternal(DataInputStream inp, Charset charset) throws IOException {
        return readStringInternal(inp, charset, 32767);
    }

    private String readStringInternal(DataInputStream inp, Charset charset, int size) throws IOException {
        int max = size * 3;
        int length = this.readVarIntInternal(inp);
        if (length > max) {
            //TODO: error because buffer length is longer than max
        } else if (length < 0) {
            //TODO: error because length is below 0 (weird string)
        } else {
            byte[] bytes = new byte[length];
            inp.readFully(bytes);
            String s = new String(bytes, charset);

            if (s.length() > size) {
                //TODO: error because string length is longer than max
            }

            return s;
        }
        return null;
    }

    default int calculateStringSize(String value) {
        return calculateStringSize(value, StandardCharsets.UTF_8);
    }

    default int calculateStringSize(String value, Charset charset) {
        if (value.isEmpty()) return 1;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeString(value, charset);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate String size:");
            return -1;
        }
    }

    default Identifier readIdentifier(DataInputStream inp) throws IOException {
        String s = this.readStringInternal(inp, StandardCharsets.UTF_8);
        String[] split = s.split(":");
        Identifier i;
        if (!s.contains(":")) {
            i = new Identifier(split[0]);
        } else {
            i = new Identifier(split[0], split[1]);
        }
        return i;
    }

    default int calculateIdentifierSize(Identifier value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            String data = value.toString();
            if (value.isWasMissingNamespace()) {
                data = data.split(":")[1];
            }
            dataOut.writeString(data);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate Identifier size:");
            return -1;
        }
    }

    default CompoundTag readNBTCompound(DataInputStream inp, int size) throws IOException {
        byte[] bytes = inp.readNBytes(size);

        NBTInputStream nbtInputStream = new NBTInputStream(new ByteArrayInputStream(bytes));
        return (CompoundTag) nbtInputStream.readRawTag(999);
    }

    default Chat readChat(DataInputStream inp) {
        return null;
    }

    default int calculateNBTCompoundSize(CompoundTag value) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            MinecraftOutputStream dataOut = new MinecraftOutputStream(byteOut);

            dataOut.writeNBTCompound(value);

            dataOut.close();
            byteOut.close();

            return byteOut.size();
        } catch (IOException e) {
            Logger.error(e,"Error while trying to calculate NBT Compound size:");
            return -1;
        }
    }
}

class Chat {
    String text;

    public Chat(Tag<?> tag) {
        if (tag instanceof CompoundTag) {
            text = tag.toString();
        } else {
            text = tag.toString();
        }
    }

    public String getText() {
        return text;
    }
}