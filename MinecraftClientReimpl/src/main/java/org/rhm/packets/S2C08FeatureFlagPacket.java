package org.rhm.packets;

import org.rhm.Identifier;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C08FeatureFlagPacket extends MinecraftServerPacket {
    public S2C08FeatureFlagPacket() {
        super(0x08);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        int total = readVarInt(inp);
        for (int i = 0; i < total; i++) {
            Identifier feature = readIdentifier(inp);
        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
