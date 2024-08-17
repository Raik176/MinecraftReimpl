package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C01EncryptionRequestPacket extends MinecraftServerPacket {
    private String serverId;
    private byte[] publicKey;
    private byte[] verifyToken;

    public S2C01EncryptionRequestPacket() {
        super(0x01);
    }

    public String getServerId() {
        return serverId;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getVerifyToken() {
        return verifyToken;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        this.serverId = readString(in, 20);

        int publicKeyLength = readVarInt(in);
        publicKey = readNBytes(in, publicKeyLength);

        int verifyTokenLength = readVarInt(in);
        verifyToken = readNBytes(in, verifyTokenLength);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
