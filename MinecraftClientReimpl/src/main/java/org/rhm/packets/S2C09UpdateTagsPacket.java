package org.rhm.packets;

import org.rhm.Identifier;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

//TODO: fix this mess, implement, fix
public class S2C09UpdateTagsPacket extends MinecraftServerPacket {
    public S2C09UpdateTagsPacket() {
        super(0x09);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        int len = readVarInt(in);
        for (int i = 0; i < len; i++) {
            Identifier identifier = readIdentifier(in);
            int tags = readVarInt(in);
            for (int j = 0; j < tags; j++) {
                Identifier tagIdentifier = readIdentifier(in);
                int count = readVarInt(in);
            }
        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
