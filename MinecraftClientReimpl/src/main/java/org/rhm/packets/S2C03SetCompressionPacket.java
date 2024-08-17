package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C03SetCompressionPacket extends MinecraftServerPacket {
    public int compressionThreshold = -1;

    public S2C03SetCompressionPacket() {
        super(0x03);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        compressionThreshold = readVarInt(inp);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}