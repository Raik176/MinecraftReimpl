package org.rhm.packets;

import net.querz.nbt.tag.CompoundTag;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C05RegistryDataPacket extends MinecraftServerPacket {
    public S2C05RegistryDataPacket() {
        super(0x05);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        CompoundTag tag = readNBTCompound(in, dataSizeRemaining);
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }
}
