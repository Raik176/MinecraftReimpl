package org.rhm.packets;

import org.rhm.utils.Logger;
import org.rhm.utils.MinecraftOutputStream;

import java.io.DataInputStream;
import java.io.IOException;

public class S2C0BChangeDifficultyPacket extends MinecraftServerPacket {
    private Difficulty difficulty;
    private boolean difficultyLocked;

    public S2C0BChangeDifficultyPacket() {
        super(0x0B);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isDifficultyLocked() {
        return difficultyLocked;
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        int difficulty = readUnsignedByte(in);
        difficultyLocked = readBoolean(in);

        switch (difficulty) {
            case 0:
                this.difficulty = Difficulty.Peaceful;
                break;
            case 1:
                this.difficulty = Difficulty.Easy;
                break;
            case 2:
                this.difficulty = Difficulty.Normal;
                break;
            case 3:
                this.difficulty = Difficulty.Hard;
                break;
            default:
                Logger.error("Received invalid difficulty from server: " + difficulty);
        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }

    public enum Difficulty {
        Peaceful,
        Easy,
        Normal,
        Hard
    }
}
