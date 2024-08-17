package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S24PongPacket extends MinecraftClientPacket {
    private final int id;

    public C2S24PongPacket(int id) {
        super(0x24);
        this.id = id;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.write(id);
    }
}
