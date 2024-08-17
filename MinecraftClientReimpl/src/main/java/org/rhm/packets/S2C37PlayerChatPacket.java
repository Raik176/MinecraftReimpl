package org.rhm.packets;

import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class S2C37PlayerChatPacket extends MinecraftServerPacket {
    public S2C37PlayerChatPacket() {
        super(0x37);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        Logger.info("test");
        //Header
        UUID sender = readUUID(inp);
        int index = readVarInt(inp);
        boolean hasSignature = readBoolean(inp);
        byte[] signature = hasSignature ? readNBytes(inp, 256) : null;
        //Body
        String message = readString(inp, 256);
        long timestamp = readLong(inp);
        long salt = readLong(inp);
        //Previous messages
        int totalPrevious = readVarInt(inp);
        for (int i = 0; i < totalPrevious; i++) {
            int messageId = readVarInt(inp);
            if (messageId == 0) readNBytes(inp, 256);
        }
        //Other
        boolean hasUnsigned = readBoolean(inp);
        // = hasUnsigned ? readString(inp) : null; TODO: need chat read thing
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
