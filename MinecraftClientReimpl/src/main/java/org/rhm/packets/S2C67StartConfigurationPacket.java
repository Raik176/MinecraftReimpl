package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C67StartConfigurationPacket extends MinecraftServerPacket {
    public S2C67StartConfigurationPacket() {
        super(0x67);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
