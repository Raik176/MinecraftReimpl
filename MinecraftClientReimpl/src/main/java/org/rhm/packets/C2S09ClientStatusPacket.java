package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class C2S09ClientStatusPacket extends MinecraftClientPacket {
    private Action action;
    public enum Action {
        RESPAWN(0),
        REQUEST_STATS(1);

        private final int value;

        Action(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public C2S09ClientStatusPacket(Action action) {
        super(0x09);

        this.action = action;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {

    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {
        out.writeVarInt(action.getValue());
    }
}
