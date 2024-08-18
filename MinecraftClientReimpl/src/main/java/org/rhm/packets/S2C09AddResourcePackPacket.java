package org.rhm.packets;

import net.querz.nbt.tag.Tag;
import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

//TODO: fix and actually implement
public class S2C09AddResourcePackPacket extends MinecraftServerPacket {
    public S2C09AddResourcePackPacket() {
        super(0x09);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        UUID uuid = readUUID(in);
        String url = readString(in);
        String hash = readString(in);
        boolean forced = readBoolean(in);
        boolean customPrompt = readBoolean(in);
        Tag<?> prompt = null;
        if (customPrompt) {
            // prompt = readChat(in, dataSizeRemaining - 1);
            // chat/tags whatever fucking suck man
        }

        Logger.info(uuid);
        Logger.info(url);
        Logger.info(hash);
        Logger.info(forced);
        Logger.info(customPrompt);
        //Logger.info(customPrompt ? prompt : "N/A");
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
