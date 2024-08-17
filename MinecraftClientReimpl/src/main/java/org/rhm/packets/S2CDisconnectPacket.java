package org.rhm.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.rhm.ClientMain;
import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.PlayState;
import org.rhm.utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;

public class S2CDisconnectPacket extends MinecraftServerPacket {
    public S2CDisconnectPacket() {
        super(ClientMain.curState == PlayState.LOGIN ? 0x00 : (ClientMain.curState == PlayState.CONFIGURATION ? 0x01 : 0x1B));
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        String str = readString(inp);
        Logger.debug("Raw disconnect string:");
        Logger.debug(str, false);

        JsonElement elem = Utils.gson.fromJson(str, JsonElement.class);
        if (elem instanceof JsonObject) {
            Logger.error("Disconnected because:\n" + ((JsonObject) elem).get("text").getAsString());
        } else {
            Logger.error("Disconnected because:\n" + elem.toString());
        }
        System.exit(0);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
