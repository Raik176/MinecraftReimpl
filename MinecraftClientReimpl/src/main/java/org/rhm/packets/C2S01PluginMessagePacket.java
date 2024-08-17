package org.rhm.packets;

import org.rhm.Identifier;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S01PluginMessagePacket extends MinecraftClientPacket {
    private final Identifier ident;
    private final byte[] msgData;

    public C2S01PluginMessagePacket(Identifier namespace, byte[] data) {
        super(0x01);
        this.ident = namespace;
        this.msgData = data;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeIdentifier(ident);
        out.write(msgData);
    }
}
