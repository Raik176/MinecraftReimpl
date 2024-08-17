package org.rhm;

import java.util.UUID;

enum LoginType {
    Yggdrasil(null), //FIXME: Not implemented yet
    EasyMC("https://sessionserver.easymc.io"),
    TheAltening(null), //FIXME: Not implemented yet
    Microsoft("https://sessionserver.mojang.com"),
    Offline(null);

    private final String sessionServer;

    LoginType(String sessionServer) {
        this.sessionServer = sessionServer;
    }

    public String getSessionServer() {
        return sessionServer;
    }
}

public class LoginData {
    public String playerName;
    public UUID playerUuid;
    public String session;
    public PlayerCerts certificates;
}
