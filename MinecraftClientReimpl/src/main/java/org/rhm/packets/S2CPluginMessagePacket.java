package org.rhm.packets;

import org.rhm.Identifier;
import org.rhm.ClientMain;
import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.PlayState;

import java.io.DataInputStream;
import java.io.IOException;

public class S2CPluginMessagePacket extends MinecraftServerPacket {
    public S2CPluginMessagePacket() {
        super(ClientMain.curState == PlayState.PLAY ? 0x18 : 0x00);
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        Identifier channel = readIdentifier(inp);
        byte[] data = readNBytes(inp, dataSizeRemaining);

        Logger.debug("Received plugin message: ");
        Logger.debug("  - Channel: " + channel);
        Logger.debug("  - Data: " + new String(data).trim());
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
