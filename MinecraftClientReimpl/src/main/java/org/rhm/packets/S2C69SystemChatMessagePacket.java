package org.rhm.packets;

import net.querz.nbt.tag.Tag;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C69SystemChatMessagePacket extends MinecraftServerPacket {
    private Tag<?> content;
    private boolean isActionBar;

    public S2C69SystemChatMessagePacket() {
        super(0x69);
    }

    public Tag<?> getContent() {
        return content;
    }

    public boolean isActionBar() {
        return isActionBar;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        content = readChat(in, dataSizeRemaining - 1);
        isActionBar = readBoolean(in);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
