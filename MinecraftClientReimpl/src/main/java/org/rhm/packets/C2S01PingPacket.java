package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S01PingPacket extends MinecraftClientPacket {
    public C2S01PingPacket() {
        super(0x01);
    }

    @Override
    protected void read(DataInputStream inp) {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeLong(System.currentTimeMillis());
    }
}
