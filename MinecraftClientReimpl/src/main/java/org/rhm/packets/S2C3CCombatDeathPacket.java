package org.rhm.packets;

import org.rhm.ClientMain;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C3CCombatDeathPacket extends MinecraftServerPacket {
    private int playerID;

    public S2C3CCombatDeathPacket() {
        super(0x3C);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        playerID = ClientMain.utils.readVarInt(in);
        in.readNBytes(dataSizeRemaining);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
