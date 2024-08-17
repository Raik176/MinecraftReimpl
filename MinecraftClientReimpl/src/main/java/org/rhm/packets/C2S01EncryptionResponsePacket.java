package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S01EncryptionResponsePacket extends MinecraftClientPacket {
    private final byte[] encryptedSecret;
    private final byte[] encryptedVerify;

    public C2S01EncryptionResponsePacket(byte[] encryptedSecret, byte[] encryptedVerify) {
        super(0x01);

        this.encryptedSecret = encryptedSecret;
        this.encryptedVerify = encryptedVerify;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeVarInt(encryptedSecret.length);
        out.write(encryptedSecret);
        out.writeVarInt(encryptedVerify.length);
        out.write(encryptedVerify);
    }
}
