package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

//TODO: fix and implement
public class S2C27ParticlePacket extends MinecraftServerPacket {
    public S2C27ParticlePacket() {
        super(0x27);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        boolean hasLongDistance = readBoolean(in);
        double x = readDouble(in);
        double y = readDouble(in);
        double z = readDouble(in);
        float offsetX = readFloat(in);
        float offsetY = readFloat(in);
        float offsetZ = readFloat(in);
        float maxSpeed = readFloat(in);
        int particleCount = readInt(in);
        int particleId = readVarInt(in);
        //TODO: dont drop particle data
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
