package org.rhm.packets;

import net.querz.nbt.tag.Tag;
import org.rhm.ClientMain;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.PlayState;

import java.io.DataInputStream;
import java.io.IOException;


//TODO: add getters, probably dont exit program
public class S2CDisconnectPacket extends MinecraftServerPacket {
    private Tag<?> reason;

    public S2CDisconnectPacket() {
        super(ClientMain.curState == PlayState.LOGIN ? 0x00 : (ClientMain.curState == PlayState.CONFIGURATION ? 0x01 : 0x1B));
    }

    public Tag<?> getReason() {
        return reason;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        reason = readChat(in, dataSizeRemaining);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
