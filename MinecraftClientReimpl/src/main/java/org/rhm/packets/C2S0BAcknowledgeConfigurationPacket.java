package org.rhm.packets;

import org.rhm.ClientMain;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.PlayState;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S0BAcknowledgeConfigurationPacket extends MinecraftClientPacket {
    public C2S0BAcknowledgeConfigurationPacket() {
        super(0x0B);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        ClientMain.curState = PlayState.CONFIGURATION;
    }
}
