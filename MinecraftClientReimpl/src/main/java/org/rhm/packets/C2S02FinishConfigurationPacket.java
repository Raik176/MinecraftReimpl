package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S02FinishConfigurationPacket extends MinecraftClientPacket {
    public C2S02FinishConfigurationPacket() {
        super(0x02);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
