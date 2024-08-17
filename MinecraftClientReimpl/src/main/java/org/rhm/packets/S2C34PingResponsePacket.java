package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C34PingResponsePacket extends MinecraftServerPacket {
    long payload;

    public S2C34PingResponsePacket() {
        super(0x34);
    }

    public long getPayload() {
        return payload;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        this.payload = readLong(inp);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
