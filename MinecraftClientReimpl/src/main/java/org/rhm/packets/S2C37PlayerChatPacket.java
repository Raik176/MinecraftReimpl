package org.rhm.packets;

import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


//TODO: fix and implement
public class S2C37PlayerChatPacket extends MinecraftServerPacket {
    public S2C37PlayerChatPacket() {
        super(0x37);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        UUID sender = readUUID(in);
        int index = readVarInt(in);
        boolean isSigned = readBoolean(in);
        byte[] messageSignature;
        if (isSigned) messageSignature = readNBytes(in,256);
        String message = readString(in,256);
        long timestamp = readLong(in);
        long salt = readLong(in);
        int totalPrevious = readVarInt(in);
        if (totalPrevious > 20) { // TODO: error

        }
        Map<Integer, byte[]> previousMessages = new HashMap<>();
        for (int i = 0; i < totalPrevious; i++) {
            previousMessages.put(readVarInt(in),readNBytes(in,256));
        }
        boolean hasUnsigned = readBoolean(in);
        String unsignedContent = "";
        if (hasUnsigned) unsignedContent = readString(in);
        int filterType = readVarInt(in);
        BitSet filterTypeBits = readBitset(in);
        int chatType = readVarInt(in);
        String senderName = readString(in);
        boolean hasTargetName = readBoolean(in);
        String targetName = "";
        if (!hasTargetName) targetName = readString(in); //FIXME: idk why i have to ! it

        Logger.info((isSigned ? "" : "[NOT SECURE] ") + sender.toString() + ": " + message);
        System.exit(0);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
