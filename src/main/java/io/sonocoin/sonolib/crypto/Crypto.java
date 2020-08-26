package io.sonocoin.sonolib.crypto;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import io.sonocoin.sonolib.misc.Helpers;

import java.security.*;
import java.util.Arrays;

public class Crypto {

    static final EdDSANamedCurveSpec ed25519 = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
    static final int SEED_LEN = 32;

    private static EdDSAPrivateKey secretFromKeys(CryptoKeys keys) {
        EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(keys.seed(), ed25519);
        return new EdDSAPrivateKey(privKey);
    }

    public static CryptoKeys generateKeys() throws NoSuchAlgorithmException {
        // @TODO maybe rewrite problems with some android versions
//        SecureRandom sr = SecureRandom.getInstanceStrong();
        SecureRandom sr = new SecureRandom();
        byte[] seed = new byte[SEED_LEN];
        sr.nextBytes(seed);

        return Crypto.generateFromSeed(seed);
    }

    public static CryptoKeys generateFromSeed(byte[] seed) {
        EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(seed, ed25519);
        EdDSAPrivateKey sKey = new EdDSAPrivateKey(privKey);
        byte[] publicKey = sKey.getAbyte();
        byte[] secretKey = Helpers.concat(seed, publicKey);

        return new CryptoKeys(secretKey, publicKey);
    }

    public static CryptoKeys generateFromSk(byte[] secret) {
        byte[] publicKey = Arrays.copyOfRange(secret, 32, 64);
        return new CryptoKeys(secret, publicKey);
    }

    public static byte[] sign(CryptoKeys keys, byte[] msg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));

        EdDSAPrivateKey sKey = Crypto.secretFromKeys(keys);

        sgr.initSign(sKey);

        sgr.update(msg);
        return sgr.sign();
    }

}
