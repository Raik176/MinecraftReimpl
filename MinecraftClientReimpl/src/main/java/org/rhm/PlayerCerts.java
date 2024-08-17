package org.rhm;

import com.google.gson.JsonObject;
import org.rhm.utils.Logger;
import org.rhm.utils.Utils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class PlayerCerts {
    private KeyPair keyPair;
    private String publicKeySignature;
    private String publicKeySignatureV2;

    public PlayerCerts(String accessToken) {
        new Thread(() -> {
            while (true) {
                try {
                    URL url = new URL("https://api.minecraftservices.com/player/certificates");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Authorization", "bearer " + accessToken);

                    con.connect();

                    JsonObject res = Utils.gson.fromJson(new InputStreamReader(con.getInputStream()), JsonObject.class);

                    String privateKeyString = res.getAsJsonObject("keyPair").get("privateKey").getAsString()
                            .replaceAll("-----BEGIN RSA PRIVATE KEY-----\\n|-----END RSA PRIVATE KEY-----\\n|\\s+", "");

                    String publicKeyString = res.getAsJsonObject("keyPair").get("publicKey").getAsString()
                            .replaceAll("-----BEGIN RSA PUBLIC KEY-----\\n|-----END RSA PUBLIC KEY-----\\n|\\s+", "");

                    PrivateKey privateKey = KeyFactory.getInstance("RSA")
                            .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString)));

                    PublicKey publicKey = KeyFactory.getInstance("RSA")
                            .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyString)));


                    this.keyPair = new KeyPair(publicKey, privateKey);
                    this.publicKeySignature = res.get("publicKeySignature").getAsString();
                    this.publicKeySignatureV2 = res.get("publicKeySignatureV2").getAsString();

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSSX");
                    LocalDateTime targetTime = LocalDateTime.parse(res.get("expiresAt").getAsString(), formatter);

                    Thread.sleep(Duration.between(LocalDateTime.now(), targetTime).toMillis());
                } catch (Exception e) {
                    Logger.error(e, "Error while updating certs:");
                }
            }
        }).start();
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public String getPublicKeySignature() {
        return publicKeySignature;
    }

    public String getPublicKeySignatureV2() {
        return publicKeySignatureV2;
    }
}
