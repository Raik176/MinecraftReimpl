package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C1ASetPlayerGroundPacket extends MinecraftServerPacket {
    boolean onGround;

    public S2C1ASetPlayerGroundPacket() {
        super(0x1A);
    }

    public boolean isOnGround() {
        return onGround;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        onGround = readBoolean(in);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
