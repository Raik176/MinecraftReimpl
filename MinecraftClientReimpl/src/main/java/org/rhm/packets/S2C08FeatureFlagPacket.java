package org.rhm.packets;

import org.rhm.Identifier;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C08FeatureFlagPacket extends MinecraftServerPacket {
    private int total;
    private Identifier[] features;

    public S2C08FeatureFlagPacket() {
        super(0x08);
    }

    public int getTotal() {
        return total;
    }

    public Identifier[] getFeatures() {
        return features;
    }

    @Override
    protected void read(DataInputStream inp) throws IOException {
        total = readVarInt(inp);
        features = new Identifier[total];
        for (int i = 0; i < total; i++) {
            Identifier feature = readIdentifier(inp);
            features[i] = feature;
        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
