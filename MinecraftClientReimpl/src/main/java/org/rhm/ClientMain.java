package org.rhm;

import com.google.gson.JsonObject;
import org.rhm.packets.*;
import org.rhm.utils.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Map.entry;
import static org.rhm.utils.Utils.gson;


public class ClientMain {
    public static final Map<PlayState, Map<Integer, Class<? extends MinecraftClientPacket>>> packetRegistry = new HashMap<>();
    public static final Map<PlayState, Map<Integer, Class<? extends MinecraftServerPacket>>> packetRegistryServer = new HashMap<>();
    public static final LoginType loginType = LoginType.Offline;
    public static final boolean status = false;
    public static final boolean warnUnknown = true;
    public static PlayState curState = PlayState.STATUS;
    public static LoginData loginData = null;
    public static Utils utils;
    public static SecretKey secretKey;

    public static void main(String[] args) throws IOException {
        utils = new Utils();
        Scanner scanner = new Scanner(System.in);

        //TODO: important Login (play)
        //Also: Player Info Update

        Logger.info("Registering packets...");
        packetRegistryServer.put(PlayState.STATUS, Map.ofEntries( //Stage 0, Status
                entry(0x00, S2C00StatusPacket.class),
                entry(0x01, S2C01PongPacket.class)
        ));
        packetRegistryServer.put(PlayState.LOGIN, Map.ofEntries( //Stage 1, login
                entry(0x00, S2CDisconnectPacket.class), //Disconnect
                entry(0x01, S2C01EncryptionRequestPacket.class), //Encryption Request
                entry(0x02, S2C02LoginSuccessPacket.class),
                entry(0x03, S2C03SetCompressionPacket.class)
        ));
        packetRegistryServer.put(PlayState.CONFIGURATION, Map.ofEntries( //Stage 2, configuration
                entry(0x00, S2CPluginMessagePacket.class),
                entry(0x01, S2CDisconnectPacket.class),
                entry(0x02, S2C02FinishConfigurationPacket.class),
                entry(0x03, S2CKeepAlivePacket.class),
                entry(0x05, S2C05RegistryDataPacket.class),
                // 0x06 Remove Resource Pack
                entry(0x07, S2C09AddResourcePackPacket.class),
                entry(0x08, S2C08FeatureFlagPacket.class)
                // 0x09 Update Tags
        ));
        packetRegistryServer.put(PlayState.PLAY, Map.ofEntries( //Stage 3, play
                entry(0x00, S2C00BundleDelimiterPacket.class),
                entry(0x01, S2C01SpawnEntityPacket.class),
                entry(0x18, S2CPluginMessagePacket.class),
                entry(0x1B, S2CDisconnectPacket.class),
                entry(0x24, S2CKeepAlivePacket.class),
                entry(0x33, S2C33PingPacket.class),
                entry(0x34, S2C34PingResponsePacket.class),
                entry(0x67, S2C67StartConfigurationPacket.class),
                entry(0x37, S2C37PlayerChatPacket.class),
                entry(0x49, S2C49ServerDataPacket.class),
                entry(0x51, S2C51HeldItemPacket.class),
                entry(0x69, S2C69SystemChatMessagePacket.class),
                entry(0x27, S2C27ParticlePacket.class),
                entry(0x0B, S2C0BChangeDifficultyPacket.class),
                entry(0x4F, S2C4FBorderWarnDistancePacket.class),
                entry(0x1A, S2C1ASetPlayerGroundPacket.class),
                entry(0x3D, S2C3DLookAtPacket.class),
                entry(0x1C, S2C1CDisguisedChatMessagePacket.class),
                entry(0x3C, S2C3CPlayerInfoUpdatePacket.class)
        ));

        try { //Debug code to check for missing, not registerd packets reutilized this from the automatic registering
            List<String> allServerPackets = new ArrayList<>();
            List<String> registeredServerPackets = new ArrayList<>();

            String location = (ClientMain.class.getPackage().getName() + ".packets").replace('.', '/');
            Enumeration<URL> packets = Thread.currentThread().getContextClassLoader().getResources(location);

            Collections.list(packets).stream()
                    .map(url -> new File(url.getFile()))
                    .filter(File::exists)
                    .flatMap(directory -> Arrays.stream(directory.listFiles()))
                    .filter(f -> f.getName().endsWith(".class"))
                    .map(f -> location + "/" + f.getName().replaceFirst("\\.class$", ""))
                    .forEach(clazz -> {
                        try {
                            if (!clazz.endsWith("MinecraftClientPacket") &&
                                    !clazz.endsWith("MinecraftServerPacket") &&
                                    !clazz.endsWith("MinecraftPacket")) {

                                Class<?> c = Class.forName(clazz.replace('/', '.'));
                                if (MinecraftServerPacket.class.isAssignableFrom(c)) {
                                    allServerPackets.add(c.getName());
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            Logger.error("Error while trying to load class at location " + clazz);
                            Logger.error(e);
                        }
                    });

            packetRegistryServer.forEach((playState, packetMap) ->
                    packetMap.values().forEach(packetClass -> registeredServerPackets.add(packetClass.getName()))
            );

            allServerPackets.stream()
                    .filter(packet -> !registeredServerPackets.contains(packet))
                    .forEach(packet -> Logger.debug("Class `" + packet + "` is not registered!"));

        } catch (Exception e) {
            Logger.error(e, "Error while trying to check for missing registered packets:");
        }

        if (!status) {
            loginData = new LoginData();
            switch (loginType) {
                case EasyMC:
                    Logger.info("Please enter an alt token (EasyMC):");
                    String token = scanner.nextLine();
                    URL url = new URL("https://api.easymc.io/v1/token/redeem");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    try (OutputStream out = con.getOutputStream()) {
                        out.write(("{\"token\":\"" + token + "\"}").getBytes(StandardCharsets.UTF_8));
                        out.flush();
                    }

                    con.connect();

                    String out;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        out = response.toString();
                    }
                    JsonObject session = gson.fromJson(out, JsonObject.class).getAsJsonObject();

                    loginData.playerName = session.get("mcName").getAsString();
                    loginData.playerUuid = UUID.fromString(session.get("uuid").getAsString());
                    loginData.session = session.get("session").getAsString();

                    break;
                case Offline:
                    Logger.info("Please enter a name to use for offline (cracked mode):");
                    loginData.playerName = scanner.nextLine();
                    loginData.playerUuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + loginData.playerName).getBytes(StandardCharsets.UTF_8));
                    break;
                case Microsoft:
                    MicrosoftLogin login = new MicrosoftLogin();
                    String accessToken = login.getAccessToken();
                    Map.Entry<String, String> xblData = login.getXblToken(accessToken);
                    String xstsToken = login.getXSTSToken(xblData.getValue(), xblData.getKey());
                    String minecraftToken = login.getMinecraftAuthToken(xstsToken, xblData.getKey());
                    if (!login.hasPurchasedMinecraft(minecraftToken)) { //User logged in with a microsoft account which doesn't own minecraft
                        Logger.error("That microsoft account does not own minecraft.");
                        System.exit(0);
                    }
                    Map.Entry<String, UUID> profileData = login.getMinecraftProfile(minecraftToken);

                    loginData.playerName = profileData.getKey();
                    loginData.playerUuid = profileData.getValue();
                    loginData.session = minecraftToken;
                    loginData.certificates = new PlayerCerts(minecraftToken);

                    break;
                default:
                    throw new UnsupportedOperationException("This authentication method is currently not supported yet");
            }
            if (!loginData.playerName.matches("^[a-zA-Z0-9_]{2,16}$")) {
                Logger.error("Got invalid username: " + loginData.playerName + " that is not a valid username, minecraft servers cannot accept it.");
                System.exit(0);
            }
            Logger.info("Authenticated using:");
            Logger.info("  - Name: " + loginData.playerName);
            Logger.info("  - UUID: " + loginData.playerUuid);
        }

        /*
        new Thread(() -> {
            while (true) {
                String command = scanner.nextLine();
            }
        }).start(); //Command thread;
         */

        Logger.info("Please enter a server ip you wish to connect to:");
        String address = scanner.nextLine();
        int port = 25565;

        if (address.contains(":")) {
            String[] split = address.split(":");
            address = split[0];
            try {
                port = Integer.parseInt(split[1]);
            } catch (Exception e) {
                Logger.error("That is not a valid number.");
                System.exit(0);
            }
        }

        Logger.info("Trying to connect to server...");
        InetSocketAddress host = new InetSocketAddress(address, port);
        try (Socket socket = new Socket()) {
            Cipher encryption = null;
            Cipher decryption = null;
            try {
                secretKey = EncryptionUtils.generateSecretKey();

                decryption = EncryptionUtils.getCipher(2, secretKey);
                encryption = EncryptionUtils.getCipher(1, secretKey);
            } catch (Exception e) {
                Logger.error(e, "Could not create a secret key:");
                System.exit(0);
            }

            socket.connect(host, 3000);
            AtomicReference<MinecraftOutputStream> output = new AtomicReference<>(new MinecraftOutputStream(socket.getOutputStream()));
            AtomicReference<DataInputStream> input = new AtomicReference<>(new DataInputStream(socket.getInputStream()));

            Logger.info("Success!");
            System.out.println();

            AtomicLong lastKeepAlive = new AtomicLong(-1);

            C2S00HandshakePacket packet = new C2S00HandshakePacket(address, port, status);
            packet.writeToStream(output.get());

            if (false) { //TODO: add a toggle for the meteor swarm thing, maybe also add ip/port prompts
                new Thread(() -> {
                    try {
                        Socket s = new Socket("127.0.0.1", 6969);

                        try (DataInputStream in = new DataInputStream(s.getInputStream())) {
                            while (true) {
                                Logger.debug("Got command: " + in.readUTF());
                            }
                        }
                    } catch (Exception e) {

                    }
                }).start();
            }

            if (status) { //Request packet
                Logger.info("Sending request packet");
                output.get().writeByte(0x01);
                output.get().writeByte(0x00);
            } else {
                curState = PlayState.LOGIN;
                C2S00LoginStartPacket packet1 = new C2S00LoginStartPacket(loginData.playerName, loginData.playerUuid);
                packet1.writeToStream(output.get());
            }

            while (true) {
                long curTime = System.currentTimeMillis();
                if (lastKeepAlive.get() != -1 && curTime - lastKeepAlive.get() > (20 * 1000)) {
                    Logger.error("Server did not respond for 20 seconds, closing connection.");
                    System.exit(0);
                    break;
                }
                int size; //packet
                int dSize; //data
                int packetId;

                size = utils.readVarInt(input.get());

                byte[] temp;

                if (CompressionUtils.compressionThreshold > 0) { //weird ahh compressed packets
                    dSize = utils.readVarInt(input.get());
                    size -= utils.calculateVarIntSize(dSize);
                    if (dSize != 0) {
                        byte[] data = new byte[size];
                        input.get().readFully(data);

                        temp = CompressionUtils.decompress(data);
                        size = dSize;
                    } else {
                        temp = new byte[size];
                        input.get().readFully(temp);
                    }
                } else {
                    temp = new byte[size];
                    input.get().readFully(temp);
                }

                DataInputStream packetData = new DataInputStream(new ByteArrayInputStream(temp));

                packetId = utils.readVarInt(packetData);
                size -= utils.calculateVarIntSize(packetId);
                if (packetId == -1) {
                    throw new IOException("Premature end of stream.");
                }

                Map<Integer, Class<? extends MinecraftServerPacket>> stage = packetRegistryServer.get(curState);
                if (stage.containsKey(packetId)) {
                    Class<? extends MinecraftPacket> clazz = stage.get(packetId);
                    MinecraftPacket instance = (MinecraftPacket) clazz.getDeclaredConstructors()[0].newInstance();

                    Logger.info("Got S2C packet with id " + packetId + " (" + clazz.getSimpleName() + ")");

                    instance.readFromStream(packetData, size);

                    if (instance instanceof S2C69SystemChatMessagePacket) {
                        S2C69SystemChatMessagePacket packet1 = (S2C69SystemChatMessagePacket) instance;

                        Logger.info((packet1.isActionBar() ? "[Action Bar]\n" : "") + utils.formatColorAll(packet1.getContent().toString()));
                    } else if (instance instanceof S2C49ServerDataPacket) {
                        S2C49ServerDataPacket packet1 = (S2C49ServerDataPacket) instance;

                        Logger.info("Got server status:");
                        Logger.info("  - Icon: " + Arrays.toString(packet1.getIcon()));
                        Logger.info("  - Enforces secure chat: " + packet1.isEnforcesSecureChat());
                        Logger.info("  - Description (MOTD):\n" + packet1.getDescription());

                        if (packet1.isEnforcesSecureChat()) {
                            Logger.error("This client does not yet support secure chat.");
                            System.exit(0);
                        }
                    } else if (instance instanceof S2C67StartConfigurationPacket) {
                        new C2S0BAcknowledgeConfigurationPacket().writeToStream(output.get());
                    } else if (instance instanceof S2C01EncryptionRequestPacket) {
                        if (loginType != LoginType.Offline) {
                            long start = System.currentTimeMillis();
                            S2C01EncryptionRequestPacket packet1 = (S2C01EncryptionRequestPacket) instance;

                            PublicKey serverKey = EncryptionUtils.byteToPublicKey(packet1.getPublicKey());

                            byte[] encryptedPublicKey = EncryptionUtils.encryptUsingKey(serverKey, secretKey.getEncoded());
                            byte[] encryptedVerifyToken = EncryptionUtils.encryptUsingKey(serverKey, packet1.getVerifyToken());

                            String serverHash = (new BigInteger(EncryptionUtils.digestData(packet1.getServerId(), serverKey, secretKey))).toString(16);

                            joinSession(serverHash);
                            new C2S01EncryptionResponsePacket(encryptedPublicKey, encryptedVerifyToken).writeToStream(output.get());

                            input.set(new DataInputStream(new CipherInputStream(socket.getInputStream(), decryption)));
                            output.set(new MinecraftOutputStream(new CipherOutputStream(socket.getOutputStream(), encryption)));

                            Logger.info("Switched to encrypted connection in " + (System.currentTimeMillis() - start) + "ms.");
                        } else {
                            Logger.info("Skipped Encryption Request as the current login type is Offline.");
                        }
                    } else if (instance instanceof S2C02FinishConfigurationPacket) {
                        C2S02FinishConfigurationPacket packet2 = new C2S02FinishConfigurationPacket();
                        packet2.writeToStream(output.get());
                        ClientMain.curState = PlayState.PLAY;
                    } else if (instance instanceof S2C03SetCompressionPacket) {
                        CompressionUtils.compressionThreshold = ((S2C03SetCompressionPacket) instance).getCompressionThreshold();
                        Logger.info("Received set compression packet with compression threshold of: " + CompressionUtils.compressionThreshold);
                    } else if (instance instanceof S2C02LoginSuccessPacket) {
                        S2C02LoginSuccessPacket packet1 = (S2C02LoginSuccessPacket) instance;
                        if (!loginData.playerUuid.toString().equals(packet1.getUuid().toString())) {
                            Logger.warn("Got incorrect uuid sent back (" + packet1.getUuid().toString() + "), but accepting it and replacing stored one.");
                            loginData.playerUuid = packet1.getUuid();
                        }
                        loginData.playerName = packet1.getPlayerName();
                        C2S03LoginAcknowledgedPacket pack = new C2S03LoginAcknowledgedPacket();
                        pack.writeToStream(output.get());
                        ClientMain.curState = PlayState.CONFIGURATION;
                    } else if (instance instanceof S2C33PingPacket) {
                        int id = ((S2C33PingPacket) instance).getId();
                        C2S24PongPacket packet1 = new C2S24PongPacket(id);
                        packet1.writeToStream(output.get());
                    } else if (instance instanceof S2CKeepAlivePacket) {
                        lastKeepAlive.set(curTime);
                        long id = ((S2CKeepAlivePacket) instance).getId();
                        C2SKeepAlivePacket packet1 = new C2SKeepAlivePacket(id);
                        packet1.writeToStream(output.get());
                    } else {
                        Logger.debug("Unhandled packet: " + instance.getClass().getSimpleName());
                    }
                } else {
                    byte[] bytes = packetData.readNBytes(size);
                    if (warnUnknown) {
                        Logger.warn("Unknown S2C packet id: " + packetId + " (" + String.format("0x%02X", packetId) + ") at stage: " + curState + " and length " + bytes.length);
                        if (size > 0) Logger.debug(Arrays.toString(utils.getFirstNElements(bytes, 100)));
                    }
                }
                packetData.close();
            }
        } catch (Exception e) {
            Logger.error(e, "Error during connection to server:");
        }
    }

    private static void joinSession(String serverHash) throws IOException {
        URL url = new URL(loginType.getSessionServer() + "/session/minecraft/join");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        try (OutputStream out = con.getOutputStream()) {
            JsonObject obj = new JsonObject();

            obj.addProperty("accessToken", loginData.session);
            obj.addProperty("selectedProfile", loginData.playerUuid.toString().replaceAll("-", ""));
            obj.addProperty("serverId", serverHash);

            out.write(gson.toJson(obj).getBytes(StandardCharsets.UTF_8));
            out.flush();
        }

        con.connect();

        Logger.debug("Session join status code: " + con.getResponseCode());
        if (con.getResponseCode() != 204) {
            JsonObject res = gson.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);
            Logger.error("Joining server failed: " + res.get("error").getAsString());
            System.exit(0);
        }
    }

    public static byte[] appendDataAtStart(byte[] existingData, byte[] newData) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

            // Append new data at the start
            dataOutputStream.write(newData);

            // Write the existing data
            dataOutputStream.write(existingData);

            // Get the resulting byte array
            byte[] resultByteArray = byteArrayOutputStream.toByteArray();

            // Close the streams
            dataOutputStream.close();
            byteArrayOutputStream.close();

            return resultByteArray;

        } catch (IOException e) {
            // Handle the exception or log the error
            Logger.error(e);
            return null;
        }
    }
}
