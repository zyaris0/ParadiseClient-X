package net.paradise_client.chatroom.common.cipher;

import java.security.*;
import java.util.Base64;

public class RSAKeyPairGenerator {
  private final PrivateKey privateKey;
  private final PublicKey publicKey;

  public RSAKeyPairGenerator() throws Exception {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
    keyGen.initialize(2048);
    KeyPair keyPair = keyGen.generateKeyPair();
    this.privateKey = keyPair.getPrivate();
    this.publicKey = keyPair.getPublic();
  }

  public String getPublicKey() {
    return Base64.getEncoder().encodeToString(publicKey.getEncoded());
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }
}
