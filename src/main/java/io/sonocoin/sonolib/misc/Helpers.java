package io.sonocoin.sonolib.misc;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Helpers {

    // uint32
    public static byte[] intToLittleEndian(long num) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt((int) num);
        return bb.array();
    }

    // uint32
    public static byte[] intToBigEndian(long num) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putInt((int) num);
        return bb.array();
    }

    // uint64
    public static byte[] longToLittleEndian(long num) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt((int) num);
        return bb.array();
    }

    public static byte[] toByteArrayUnsigned(BigInteger bi) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] byteArray = bi.toByteArray();

        int len = byteArray.length;

        if (len < 8) {
            byte[] empty = new byte[8 - len];
            buf.write(empty);
            buf.write(byteArray);
        } else if (len == 8) {
            buf.write(8);
        } else {
            throw new Exception("the number is longer than uint64: " + bi.toString());
        }

        return buf.toByteArray();
    }

    public static byte[] biToLittleEndian(BigInteger bi) throws Exception {
        byte[] extractedBytes = toByteArrayUnsigned(bi);
        return reverseArray(extractedBytes);
    }

    public static byte[] biToBigEndian(BigInteger bi) throws Exception {
        return toByteArrayUnsigned(bi);
    }

    public static byte[] reverseArray(byte[] in) {
        int len = in.length;
        byte[] out = new byte[len];
        for(int i = 0; i < in.length; i++){
            out[len - i -1] = in[i];
        }
        return out;
    }

    public static byte[] sha256(byte[] data) {
        return DigestUtils.sha256(data);
    }

    public static byte[] DHASH(byte[] data) {
        return DigestUtils.sha256(DigestUtils.sha256(data));
    }

    public static byte[] DHASH(byte[] data, int offset, int length) {
        byte[] arr = new byte[length];
        System.arraycopy(data, offset, data, 0, length);
        return DigestUtils.sha256(DigestUtils.sha256(data));
    }

    public static byte[] sha256hash160(byte[] input) {
        byte[] sha256 = sha256(input);
        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(sha256, 0, sha256.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] dest = new byte[a.length + b.length];
        System.arraycopy(a, 0, dest, 0, a.length);
        System.arraycopy(b, 0, dest, a.length, b.length);
        return dest;
    }

    // because android does not have encodeHexString()
    // @link https://stackoverflow.com/questions/9126567/method-not-found-using-digestutils-in-android
    public static String hex(byte[] data) {
        return new String(Hex.encodeHex(data));
    }

}
