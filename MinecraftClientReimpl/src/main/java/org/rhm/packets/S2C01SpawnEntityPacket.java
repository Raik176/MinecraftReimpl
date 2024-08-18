package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class S2C01SpawnEntityPacket extends MinecraftServerPacket {
    private int entityId;
    private UUID entityUUID;
    private int type;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private float headYaw;
    private int data;
    private short velocityX;
    private short velocityY;
    private short velocityZ;

    public S2C01SpawnEntityPacket() {
        super(0x01);
    }

    public int getEntityId() {
        return entityId;
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public int getType() {
        return type;
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

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getHeadYaw() {
        return headYaw;
    }

    public int getData() {
        return data;
    }

    public short getVelocityX() {
        return velocityX;
    }

    public short getVelocityY() {
        return velocityY;
    }

    public short getVelocityZ() {
        return velocityZ;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        entityId = readVarInt(in);
        entityUUID = readUUID(in);
        type = readVarInt(in);
        x = readDouble(in);
        y = readDouble(in);
        z = readDouble(in);
        pitch = readAngle(in);
        yaw = readAngle(in);
        headYaw = readAngle(in);
        data = readVarInt(in);
        velocityX = readShort(in);
        velocityY = readShort(in);
        velocityZ = readShort(in);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
