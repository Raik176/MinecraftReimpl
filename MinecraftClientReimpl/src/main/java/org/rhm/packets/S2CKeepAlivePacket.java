package org.rhm.packets;

import org.rhm.ClientMain;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.PlayState;

import java.io.DataInputStream;
import java.io.IOException;

public class S2CKeepAlivePacket extends MinecraftServerPacket {
    private long id;

    public S2CKeepAlivePacket() {
        super(ClientMain.curState == PlayState.CONFIGURATION ? 0x03 : 0x24);
    }

    public long getId() {
        return id;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        this.id = readLong(inp);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
