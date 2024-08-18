package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

//TODO: implement, fix and dont drop player actions
// IMPORTANT: needed to use chat!
public class S2C3EPlayerInfoUpdatePacket extends MinecraftServerPacket {
    private byte actions;
    private int numberOfPlayers;


    public S2C3EPlayerInfoUpdatePacket() {
        super(0x3e);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        actions = readNBytes(in, 1)[0];
        numberOfPlayers = readVarInt(in);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
