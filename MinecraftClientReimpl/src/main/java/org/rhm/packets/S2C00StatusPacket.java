package org.rhm.packets;

import com.google.gson.JsonObject;
import org.rhm.utils.MinecraftOutputStream;
import org.rhm.utils.Utils;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C00StatusPacket extends MinecraftServerPacket {
    private int protocol;
    private String fancyVersion;
    private int playersOnline;
    private int maxPlayers;
    private String description;

    public S2C00StatusPacket() {
        super(0x00);
        this.protocol = -1;
        this.fancyVersion = "N/A";
        this.playersOnline = -1;
        this.maxPlayers = -1;
        this.description = "N/A";
    }

    public int getProtocol() {
        return protocol;
    }

    public String getFancyVersion() {
        return fancyVersion;
    }

    public int getPlayersOnline() {
        return playersOnline;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public String getDescription() {
        return description;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        int length = readVarInt(inp);

        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }
        if (length == 0) {
            throw new IOException("Invalid string length.");
        }

        byte[] in = readNBytes(inp, length);
        JsonObject json = Utils.gson.fromJson(new String(in), JsonObject.class);

        try {
            if (json.has("version")) {
                protocol = json.getAsJsonObject("version").get("protocol").getAsInt();
                fancyVersion = json.getAsJsonObject("version").get("name").getAsString();
            }
            if (json.has("players")) {
                this.playersOnline = json.getAsJsonObject("players").get("online").getAsInt();
                this.maxPlayers = json.getAsJsonObject("players").get("max").getAsInt();
            }
            this.description = json.get("description").getAsString();
        } catch (Exception e) {

        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
