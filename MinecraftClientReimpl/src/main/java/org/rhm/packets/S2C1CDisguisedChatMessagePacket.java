package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

//TODO: implement/fix
// i have no idea why i get an EOFException whenever i read any more than the type
public class S2C1CDisguisedChatMessagePacket extends MinecraftServerPacket {
    public S2C1CDisguisedChatMessagePacket() {
        super(0x1C);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        String msg = readString(in);
        int chatType = readVarInt(in);


        System.exit(0);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
