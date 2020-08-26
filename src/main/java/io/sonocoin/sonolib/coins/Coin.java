package io.sonocoin.sonolib.coins;

import io.sonocoin.sonolib.crypto.Crypto;
import io.sonocoin.sonolib.crypto.CryptoKeys;
import io.sonocoin.sonolib.crypto.HD;
import io.sonocoin.sonolib.misc.Helpers;
import io.sonocoin.sonolib.misc.base58.AddressFormatException;
import io.sonocoin.sonolib.misc.base58.Base58;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

public class Coin {

    public final static String createCoinHex = "a2b62e59";
    public final static String transferCoinHex = "b3f4fb31";
    public final static String infoCoinHex = "b64a097e";

    public final static String approve = "approve";
    public final static String transfer = "transfer";

    public final CryptoKeys keys;

    public Coin() throws NoSuchAlgorithmException {
        this.keys = Crypto.generateKeys();
    }

    public Coin(String secretKey) throws DecoderException {
        this.keys = new CryptoKeys(Hex.decodeHex(secretKey.toCharArray()));
    }

    public Coin(byte[] secretKey) {
        this.keys = new CryptoKeys(secretKey);
    }

    public byte[] publicKey() {
        return keys.publicKey;
    }

    public String publicKeyHex() {
        return String.valueOf(Hex.encodeHex(publicKey()));
    }

    public byte[] secretKey() {
        return keys.secretKey;
    }

    public String secretKeyHex() {
        return String.valueOf(Hex.encodeHex(secretKey()));
    }

    public static String getInfoPayload(String coinPk) {
        return infoCoinHex + coinPk;
    }

    public static String getCreateCoinPayload(HD hd, String coinPk, BigInteger amount, BigInteger nonce) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        buf.write(approve.getBytes());
        byte[] amountSlice = Helpers.biToBigEndian(amount);

        buf.write(amountSlice);
        buf.write(Helpers.biToBigEndian(nonce));

        byte[] sig = Crypto.sign(hd.cryptoKeys(), buf.toByteArray());

        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(hd.publicKey());
        payload.write(amountSlice);
        payload.write(sig);

        return createCoinHex + Helpers.hex(payload.toByteArray()) + coinPk;
    }

    public static String getSpendCoinPayload(CryptoKeys keys, String receiver) throws IOException, AddressFormatException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(transfer.getBytes());

        byte[] receiverAddress = Base58.decode(receiver);

        buf.write(receiverAddress);

        byte[] sig = Crypto.sign(keys, buf.toByteArray());

        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(keys.publicKey);
        payload.write(receiverAddress);
        payload.write(sig);

        return transferCoinHex + Helpers.hex(payload.toByteArray());
    }


}
