package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C51HeldItemPacket extends MinecraftServerPacket {
    byte newSlot;

    public S2C51HeldItemPacket() {
        super(0x51);
    }

    public byte getNewSlot() {
        return newSlot;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        newSlot = readNBytes(in,1)[0];
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
