package io.sonocoin.sonolib.crypto;

import io.sonocoin.sonolib.misc.Helpers;
import io.sonocoin.sonolib.misc.base58.Base58;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Wallet {

    public final static byte[] WALLET_VERSION = new byte[]{
        (byte) 14, // S
        (byte) 48  // C
    };
    public final static int CHECK_SUM_LEN = 4;

    public String base58Address;
    public byte[] address;

    public Wallet(byte[] pubKey) throws IOException {
        ByteArrayOutputStream versionedPayload = new ByteArrayOutputStream();

        versionedPayload.write(Wallet.WALLET_VERSION);
        byte[] pkHash = Wallet.hashPublicKey(pubKey);
        versionedPayload.write(pkHash);
        byte[] checksum = Wallet.checksum(versionedPayload.toByteArray());

        versionedPayload.write(checksum);

        this.address = versionedPayload.toByteArray();
        this.base58Address = Base58.encode(this.address);
    }

    public static byte[] hashPublicKey(byte[] pubKey) {
        return Helpers.sha256hash160(pubKey);
    }

    public static byte[] checksum(byte[] payload) {
        byte[] data = Helpers.DHASH(payload);

        byte[] slice = new byte[CHECK_SUM_LEN];

        System.arraycopy(data, 0, slice, 0, slice.length);
        return slice;
    }

    public static boolean isValidAddress(String address) {
        byte[] addressBytes;
        try {
            addressBytes = Base58.decode(address);
        } catch(Exception e) {
            return false;
        }

        if (addressBytes.length < WALLET_VERSION.length + CHECK_SUM_LEN) {
            return false;
        }

        byte[] ver = Arrays.copyOfRange(addressBytes, 0, WALLET_VERSION.length);

        if (!Arrays.equals(ver, WALLET_VERSION)) {
            return false;
        }

        byte[] payload = Arrays.copyOfRange(addressBytes, 0, addressBytes.length - CHECK_SUM_LEN);
        byte[] checksum = checksum(payload);
        byte[] checkAddress = Helpers.concat(payload, checksum);

        return Arrays.equals(checkAddress, addressBytes);
    }

}
