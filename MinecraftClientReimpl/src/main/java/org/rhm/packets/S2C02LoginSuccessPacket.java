package org.rhm.packets;

import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

//TODO implement and fix
public class S2C02LoginSuccessPacket extends MinecraftServerPacket {
    private String playerName;
    private UUID uuid;

    public S2C02LoginSuccessPacket() {
        super(0x02);
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        UUID uuid = readUUID(inp);
        String name = readString(inp, 16);

        this.uuid = uuid;
        this.playerName = name;

        int size = readVarInt(inp);
        for (int i = 0; i < size; i++) {
            String string1 = readString(inp);
            String string2 = readString(inp);

            boolean isSigned = readBoolean(inp);
            Logger.debug("Unknown data: signed=" + isSigned + " s1=" + string1 + ", s2=" + string2);
            if (isSigned) {
                Logger.debug("Signed data: " + readString(inp));
            }
        }

        Logger.info("Logged in with name: " + name);
        Logger.info(uuid);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
