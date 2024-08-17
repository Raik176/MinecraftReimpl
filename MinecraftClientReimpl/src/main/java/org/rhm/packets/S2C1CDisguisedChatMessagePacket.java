package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.Logger;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C1CDisguisedChatMessagePacket extends MinecraftServerPacket {
    public S2C1CDisguisedChatMessagePacket() {
        super(0x1C);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        String msg = readString(inp);
        int chatType = readVarInt(inp);
        String senderName = readString(inp);
        boolean hasTarget = readBoolean(inp);
        String target;
        //if (hasTarget) target = readString(inp);

        Logger.info("[" + senderName.trim() + "] " + msg.trim());
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
