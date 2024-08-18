package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C3DLookAtPacket extends MinecraftServerPacket {
    private int bodyPart;
    private double x;
    private double y;
    private double z;
    private boolean isEntity;
    private int entityId;
    private int entityBodyPart;

    public S2C3DLookAtPacket() {
        super(0x3D);
    }

    public int getBodyPart() {
        return bodyPart;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public boolean isEntity() {
        return isEntity;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEntityBodyPart() {
        return entityBodyPart;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        bodyPart = readVarInt(in);
        x = readDouble(in);
        y = readDouble(in);
        z = readDouble(in);
        isEntity = readBoolean(in);
        if (isEntity) {
            entityId = readVarInt(in);
            entityBodyPart = readVarInt(in);
        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
