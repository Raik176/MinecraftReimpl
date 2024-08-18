package org.rhm.packets;

import org.rhm.utils.MinecraftOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class S2C3CPlayerInfoUpdatePacket extends MinecraftServerPacket {
    byte actions;
    int playerCount;
    Map<UUID,PlayerAction> playerActions = new HashMap<>();
    public S2C3CPlayerInfoUpdatePacket() {
        super(0xC3);
    }

    @Override
    protected void read(DataInputStream in) throws IOException {
        actions = readNBytes(in,1)[0];
        playerCount = readVarInt(in);
        for (int i = 0; i < playerCount; i++) {
            UUID uuid = readUUID(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MinecraftOutputStream data = new MinecraftOutputStream(baos);
            PlayerAction.PlayerActionType type = null;
            
            
            if ((actions & 0x01) != 0) {
                // Add Player action
                type = PlayerAction.PlayerActionType.ADD_PLAYER;
                data.writeString(readString(in,16)); // name
                int properties = readVarInt(in); // number of properties
                data.writeVarInt(properties);

                for (int j = 0; j < properties; j++) {
                    data.writeString(readString(in)); // name
                    data.writeString(readString(in)); // value
                    boolean isSigned = readBoolean(in); // is signed?
                    data.writeBoolean(isSigned);
                    if (isSigned) data.writeString(readString(in)); // signature
                }
            }
            if ((actions & 0x02) != 0) {
                // Initialize Chat action
                type = PlayerAction.PlayerActionType.INIT_CHAT;
                boolean hasSignature = readBoolean(in); // has signature?
                data.writeBoolean(hasSignature);
                if (hasSignature) {
                    data.writeUUID(readUUID(in)); // chat session id
                    data.writeLong(readLong(in)); // pubkey expiry time
                    int encodedPKeySize = readVarInt(in); // encoded pubkey size
                    if (encodedPKeySize > 512) { // error

                    }
                    data.writeVarInt(encodedPKeySize);
                    data.write(readNBytes(in,encodedPKeySize)); // encoded public key
                    int pkeySignatureSize = readVarInt(in); // pubkey signature size
                    if (pkeySignatureSize > 4096) { // error

                    }
                    data.writeVarInt(pkeySignatureSize);
                    data.write(readNBytes(in,pkeySignatureSize)); // pubkey signature
                }
            }
            if ((actions & 0x04) != 0) {
                // Update Game Mode action
                type = PlayerAction.PlayerActionType.UPDATE_GAME_MODE;
                data.writeVarInt(readVarInt(in)); // new game mode
            }
            if ((actions & 0x08) != 0) {
                // Update Listed action
                type = PlayerAction.PlayerActionType.UPDATE_LISTED;
                data.writeBoolean(readBoolean(in)); // should player be listed?
            }
            if ((actions & 0x10) != 0) {
                // Update Latency action
                type = PlayerAction.PlayerActionType.UPDATE_LATENCY;
                data.writeVarInt(readVarInt(in)); // ping in ms
            }
            if ((actions & 0x20) != 0) {
                // Update Display Name action
                type = PlayerAction.PlayerActionType.UPDATE_DISPLAY_NAME;
                boolean hasDisplay = readBoolean(in); // has display name?
                data.writeBoolean(hasDisplay);
                if (hasDisplay) data.writeString(readString(in)); // display name
            }

            data.flush();
            baos.flush();

            playerActions.put(uuid,new PlayerAction(type,baos.toByteArray()));

            baos.close();
            data.close();
        }
    }

    @Override
    protected void write(MinecraftOutputStream out) throws IOException {

    }

    public class PlayerAction {
        public final PlayerActionType type;
        public final byte[] data;

        public PlayerAction(PlayerActionType type, byte[] data) {
            this.type = type;
            this.data = data;
        }

        public enum PlayerActionType {
            ADD_PLAYER,
            INIT_CHAT,
            UPDATE_GAME_MODE,
            UPDATE_LISTED,
            UPDATE_LATENCY,
            UPDATE_DISPLAY_NAME
        }
    }
}