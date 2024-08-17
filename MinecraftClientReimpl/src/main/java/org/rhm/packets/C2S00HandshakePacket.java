package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class C2S00HandshakePacket extends MinecraftClientPacket {

    private final String serverHost;
    private final int serverPort;
    private final int nextState;

    public C2S00HandshakePacket(String host, int port, boolean isStatus) {
        super(0x00);
        this.serverHost = host;
        this.serverPort = port;
        this.nextState = isStatus ? 1 : 2;
    }

    @Override
    protected void read(DataInputStream inp) {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeVarInt(765);
        out.writeString(this.serverHost, StandardCharsets.UTF_8);
        out.writeShort(this.serverPort);
        out.writeVarInt(nextState);
    }
}
