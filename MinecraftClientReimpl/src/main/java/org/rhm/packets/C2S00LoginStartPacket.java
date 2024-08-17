package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class C2S00LoginStartPacket extends MinecraftClientPacket {

    private final String userName;
    private final UUID userUuid;

    public C2S00LoginStartPacket(String name, UUID uuid) {
        super(0x00);

        this.userName = name;
        this.userUuid = uuid;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeString(userName, StandardCharsets.UTF_8);
        out.writeUUID(userUuid);
    }
}
