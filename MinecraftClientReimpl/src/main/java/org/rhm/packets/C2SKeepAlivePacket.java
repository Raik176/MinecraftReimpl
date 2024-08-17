package org.rhm.packets;

import org.rhm.ClientMain;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.PlayState;

import java.io.DataInputStream;
import java.io.IOException;

public class C2SKeepAlivePacket extends MinecraftClientPacket {
    private final long id;

    public C2SKeepAlivePacket(long id) {
        super(ClientMain.curState == PlayState.CONFIGURATION ? 0x03 : 0x15);
        this.id = id;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeLong(id);
    }
}
