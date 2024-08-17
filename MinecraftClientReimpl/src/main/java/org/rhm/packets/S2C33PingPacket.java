package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C33PingPacket extends MinecraftServerPacket {
    private int id;

    public S2C33PingPacket() {
        super(0x33);
    }

    public int getId() {
        return id;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        this.id = readInt(inp);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
