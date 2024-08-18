package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C4FBorderWarnDistancePacket extends MinecraftServerPacket {
    private int warningBlockDistance;

    public S2C4FBorderWarnDistancePacket() {
        super(0x4F);
    }

    public int getWarningBlockDistance() {
        return warningBlockDistance;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        warningBlockDistance = readVarInt(in);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
