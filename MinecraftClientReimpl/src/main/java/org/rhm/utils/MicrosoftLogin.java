package org.rhm.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Simple class to authenticate using microsoft, and check if an account owns minecraft (Does not work for xbox gamepass yet)
 * @author RightHandMan176 (rhm176. on discord)
 */
public class MicrosoftLogin {
    private static final String XBL_AUTHENTICATE_URL = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String XSTS_AUTHORIZE_URL = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String MINECRAFT_AUTHENTICATION_URL = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String MINECRAFT_OWNERSHIP_URL = "https://api.minecraftservices.com/entitlements/mcstore";
    private static final String MINECRAFT_PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    private static final String CLIENT_ID = "4673b348-3efa-4f6a-bbb6-34e141cdc638"; //soz had to use meteors client id lol
    private static final int TIMEOUT_THRESHOLD = 60 * 1000;

    private String accessToken = "";

    /**
     * Method to request a microsoft access token from the user. Used for basic microsoft authentication
     * @return A microsoft access token
     */
    public String getAccessToken() {
        String url = "https://login.live.com/oauth20_authorize.srf?client_id=" + CLIENT_ID + "&response_type=code&redirect_uri=http://127.0.0.1:9675&scope=XboxLive.signin%20offline_access&prompt=select_account";

        Logger.info("Please open this webpage and select a minecraft account:");
        Logger.info(url, false);

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(9675), 0);

            server.createContext("/", exc -> {
                Map<String, String> params = Collections.singletonMap("code", exc.getRequestURI().getQuery());
                String code = params.getOrDefault("code", "");

                String response = code.isEmpty() ? "Missing 'code' parameter." : "You may now close this page.";
                exc.sendResponseHeaders(code.isEmpty() ? 400 : 200, response.length());
                OutputStream os = exc.getResponseBody();
                os.write(response.getBytes());
                os.flush();
                os.close();

                try {
                    URL url2 = new URL("https://login.live.com/oauth20_token.srf");
                    HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);

                    connection.getOutputStream().write(("client_id=" + CLIENT_ID + "&" + code + "&grant_type=authorization_code" +
                            "&redirect_uri=http://127.0.0.1:9675").getBytes(StandardCharsets.UTF_8));

                    connection.connect();

                    JsonObject res = Utils.gson.fromJson(new InputStreamReader(connection.getInputStream()), JsonObject.class);

                    if (!res.has("access_token")) {
                        //WTF???
                        //TODO: error handling
                        System.exit(0);
                    } else {
                        accessToken = res.get("access_token").getAsString();
                    }

                } catch (Exception e) {
                    Logger.error(e,"Error while trying to obtain a Microsoft access token:");
                    System.exit(0);
                }
            });

            server.setExecutor(null);
            server.start();

            long authenticationStart = System.currentTimeMillis();
            while (accessToken.isEmpty()) { //Block execution until user accepts anything
                try {
                    if ((System.currentTimeMillis() - authenticationStart) > TIMEOUT_THRESHOLD) { //Exit program if user does not authenticate after 60 secs
                        Logger.error("You have not authenticated after 60 seconds.");
                        System.exit(0);
                    }
                    Thread.sleep(50);
                } catch (Exception e) {
                    Logger.error(e,"Error while trying to check if user authenticated:");
                    System.exit(0);
                }
            }

            server.stop(0);

            return accessToken;
        } catch (IOException e) {
            Logger.error(e,"Error while trying to get access token:");
            System.exit(0);
        }
        return null;
    }

    /**
     * Method to obtain an xbox live access token from a microsoft access token. Used for basic xbox live methods.
     * @param accessToken The microsoft access token.
     * @return An entry containing the user hash of said xbox user and an xbox live login token.
     */
    public Map.Entry<String, String> getXblToken(String accessToken) {
        try {
            URL url = new URL(XBL_AUTHENTICATE_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            JsonObject payload = new JsonObject();
            JsonObject properties = new JsonObject();
            properties.addProperty("AuthMethod", "RPS");
            properties.addProperty("SiteName", "user.auth.xboxlive.com");
            properties.addProperty("RpsTicket", "d=" + accessToken);

            payload.add("Properties", properties);
            payload.addProperty("RelyingParty", "http://auth.xboxlive.com");
            payload.addProperty("TokenType", "JWT");

            con.connect();

            OutputStream is = con.getOutputStream();
            is.write(Utils.gson.toJson(payload).getBytes(StandardCharsets.UTF_8));
            is.flush();

            JsonObject res = Utils.gson.fromJson(new InputStreamReader(con.
                    getInputStream()), JsonObject.class);

            is.close();

            Map.Entry<String, String> out = new AbstractMap.SimpleEntry<>(
                    res.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString(),
                    res.get("Token").getAsString());

            return out;
        } catch (Exception e) {
            Logger.error(e,"Error while trying to get Xbox Live Token:");
            System.exit(0);
        }
        return null;
    }

    /**
     * Acquire an xbox live security token from a normal xbox live token without verifying the user hash.
     * @param xblToken The xbox live token.
     * @return An Xbox Live Security Token.
     */
    public String getXSTSToken(String xblToken) {
        return getXSTSToken(xblToken, null);
    }

    /**
     * Acquire an xbox live security token from a normal xbox live token
     * @param xblToken The xbox live token.
     * @param userHash The user hash of said xbox user. Used for verification, but not required
     * @return An Xbox Live Security Token.
     */
    public String getXSTSToken(String xblToken, String userHash) {
        try {
            URL url = new URL(XSTS_AUTHORIZE_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            JsonObject payload = new JsonObject();
            JsonObject properties = new JsonObject();
            JsonArray userTokens = new JsonArray();

            userTokens.add(xblToken);

            properties.addProperty("SandboxId", "RETAIL");
            properties.add("UserTokens", userTokens);

            payload.add("Properties", properties);
            payload.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
            payload.addProperty("TokenType", "JWT");

            con.connect();

            OutputStream is = con.getOutputStream();
            is.write(Utils.gson.toJson(payload).getBytes(StandardCharsets.UTF_8));
            is.flush();

            JsonObject res = Utils.gson.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);

            if (userHash != null && !res.getAsJsonObject("DisplayClaims").getAsJsonArray("xui").get(0).getAsJsonObject().get("uhs").getAsString().equals(userHash)) {
                //WTF wrong userhash??
                Logger.error("Fatal error: user hash is wrong, this might be a man in the middle attack.");
                System.exit(0);
            }

            is.close();
            return res.get("Token").getAsString();
        } catch (Exception e) {
            Logger.error(e,"Error while trying to obtain an Xbox Live Security Token:");
            System.exit(0);
        }
        return null;
    }

    /**
     * Method to obtain a Mojang access token, used for pretty much all minecraft services which require authentication.
     * @param xstsToken The xbox live security token
     * @param userHash The user hash of said xbox user. Used for verification, but not required
     * @return An minecraft (Mojang) access token.
     */
    public String getMinecraftAuthToken(String xstsToken, String userHash) {
        try {
            URL url = new URL(MINECRAFT_AUTHENTICATION_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");

            JsonObject payload = new JsonObject();

            payload.addProperty("identityToken", "XBL3.0 x=" + userHash + ";" + xstsToken);

            con.connect();

            OutputStream is = con.getOutputStream();
            is.write(Utils.gson.toJson(payload).getBytes(StandardCharsets.UTF_8));
            is.flush();

            JsonObject res = Utils.gson.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);

            is.close();

            return res.get("access_token").getAsString();
        } catch (Exception e) {
            Logger.error(e,"Error while trying to get a Mojang token:");
            System.exit(0);
        }
        return null;
    }

    /**
     * Method to check if an account actually owns minecraft. Returns false for xbox gamepass users.
     * @param minecraftToken The minecraft access token.
     * @return whether said account owns minecraft.
     */
    public boolean hasPurchasedMinecraft(String minecraftToken) { //FIXME: Returns false for xbox gamepass users.
        try {
            URL url = new URL(MINECRAFT_OWNERSHIP_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + minecraftToken);

            con.connect();

            JsonObject res = Utils.gson.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);

            return !res.getAsJsonArray("items").isEmpty();
        } catch (Exception e) {
            Logger.error(e,"Error while trying to check if account owns Minecraft:");
            System.exit(0);
        }
        return false;
    }

    /**
     * Method for profile data
     * @param minecraftToken The minecraft access token.
     * @return An entry containing the Username and UUID.
     */
    public Map.Entry<String, UUID> getMinecraftProfile(String minecraftToken) {
        try {
            URL url = new URL(MINECRAFT_PROFILE_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + minecraftToken);

            con.connect();

            JsonObject res = Utils.gson.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);

            return new AbstractMap.SimpleEntry<>(res.get("name").getAsString(), UUID.fromString(res.get("id").getAsString().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5")));
        } catch (Exception e) {
            Logger.error(e,"Error while trying to obtain profile data:");
            System.exit(0);
        }
        return null;
    }
}
