package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S03LoginAcknowledgedPacket extends MinecraftClientPacket {
    public C2S03LoginAcknowledgedPacket() {
        super(0x03);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
