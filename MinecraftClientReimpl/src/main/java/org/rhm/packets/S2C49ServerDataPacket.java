package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

//TODO: fix and implement
public class S2C49ServerDataPacket extends MinecraftServerPacket {
    private String description;
    private boolean hasIcon;
    private byte[] icon;
    private boolean enforcesSecureChat;

    public S2C49ServerDataPacket() {
        super(0x49);
    }

    public String getDescription() {
        return description;
    }

    public boolean isHasIcon() {
        return hasIcon;
    }

    public byte[] getIcon() {
        return icon;
    }

    public boolean isEnforcesSecureChat() {
        return enforcesSecureChat;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        description = readString(in);

        hasIcon = readBoolean(in);
        int size = readVarInt(in);
        if (hasIcon) icon = readNBytes(in, size - 102);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
