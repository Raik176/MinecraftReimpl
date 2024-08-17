package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C02FinishConfigurationPacket extends MinecraftServerPacket {
    public S2C02FinishConfigurationPacket() {
        super(0x02);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
