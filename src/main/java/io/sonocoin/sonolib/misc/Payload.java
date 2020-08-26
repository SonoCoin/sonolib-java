package io.sonocoin.sonolib.misc;

import io.sonocoin.sonolib.coins.CoinInfo;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.util.Arrays;

public class Payload {

    public static BigInteger toBi(String payload) throws DecoderException {
        return new BigInteger(Hex.decodeHex(payload.toCharArray()));
    }

    public static CoinInfo toCoinInfo(String payload) throws Exception {
        byte[] buf = Hex.decodeHex(payload.toCharArray());
        if (buf.length != 9) {
            throw new Exception("CoinInfo: Invalid payload size");
        }

        int status = buf[0];
        BigInteger amount = new BigInteger(Arrays.copyOfRange(buf, 1, 9));
        return new CoinInfo(status, amount);
    }

}
