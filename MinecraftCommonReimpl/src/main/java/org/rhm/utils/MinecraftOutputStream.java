package org.rhm.utils;

import net.querz.nbt.io.NBTOutputStream;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.rhm.Identifier;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.UUID;

public class MinecraftOutputStream extends DataOutputStream {
    public MinecraftOutputStream(OutputStream out) {
        super(out);
    }

    public void writeChat(Tag<?> tag) throws IOException {
        NBTOutputStream nbtOutputStream = new NBTOutputStream(this);
        nbtOutputStream.writeRawTag(tag, 999);
        nbtOutputStream.flush();
    }

    public void writeVarInt(int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                this.writeByte(paramInt);
                return;
            }

            this.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    public void writeBitset(BitSet bitSet) throws IOException {
        int length = bitSet.length();
        int longArrayLength = (length + 63) / 64;

        writeVarInt(length);

        for (int i = 0; i < longArrayLength; i++) {
            this.writeLong(bitSetToLong(bitSet, i));
        }
    }

    public void writeUUID(UUID uuid) throws IOException {
        writeUnsignedLong(uuid.getMostSignificantBits());
        writeUnsignedLong(uuid.getLeastSignificantBits());
    }

    public void writeUnsignedLong(long value) throws IOException {
        for (int i = 7; i >= 0; i--) {
            this.write((int) (value >>> (i * 8)) & 0xFF);
        }
    }

    public void writeString(String string) throws IOException {
        writeString(string, StandardCharsets.UTF_8);
    }

    public void writeString(String string, Charset charset) throws IOException {
        if (string.length() > 32767) {
            throw new IllegalArgumentException("Invalid length of input string");
        }

        byte[] bytes = string.getBytes(charset);

        if (bytes.length > (string.length() * 3) + 3) {
            throw new IllegalArgumentException("UTF-8 size exceeds the maximum allowed");
        }

        writeVarInt(bytes.length);
        this.write(bytes);
    }

    public void writeIdentifier(Identifier identifier) throws IOException {
        String namespace = identifier.getNamespace();
        String key = identifier.getKey();

        String combinedString = namespace + ":" + key;

        writeString(combinedString, StandardCharsets.UTF_8);
    }

    public void writeNBTCompound(CompoundTag tag) throws IOException {
        NBTOutputStream nbtOutputStream = new NBTOutputStream(this);
        nbtOutputStream.writeRawTag(tag, 999);
        nbtOutputStream.flush();
    }

    private long bitSetToLong(BitSet bitSet, int index) {
        long result = 0;
        int start = index * 64;
        int end = Math.min(start + 64, bitSet.length());

        for (int i = start; i < end; i++) {
            if (bitSet.get(i)) {
                result |= (1L << (i - start));
            }
        }

        return result;
    }
}
