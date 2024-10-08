package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C01PongPacket extends MinecraftServerPacket {
    private long pingtime;

    public S2C01PongPacket() {
        super(0x00);
    }

    public long getPingtime() {
        return pingtime;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        pingtime = readLong(inp);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
