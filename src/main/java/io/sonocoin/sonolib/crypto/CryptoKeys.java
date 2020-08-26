package io.sonocoin.sonolib.crypto;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

public class CryptoKeys {

    public byte[] publicKey;
    public byte[] secretKey;

    public CryptoKeys() {
    }

    public CryptoKeys(byte[] secretKey) {
        this.secretKey = secretKey;
        this.publicKey = Arrays.copyOfRange(secretKey, 32, 64);
    }

    public CryptoKeys(byte[] secretKey, byte[] publicKey) {
        this.publicKey = publicKey;
        this.secretKey = secretKey;
    }

    public String secretKeyHex() {
        return String.valueOf(Hex.encodeHex(secretKey));
    }

    public String publicKeyHex() {
        return String.valueOf(Hex.encodeHex(publicKey));
    }

    public byte[] seed() {
        return Arrays.copyOfRange(this.secretKey, 0, 32);
    }

}
