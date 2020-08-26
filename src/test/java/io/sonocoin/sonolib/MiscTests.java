package io.sonocoin.sonolib;

import io.sonocoin.sonolib.misc.Helpers;
import org.junit.Test;

import java.math.BigInteger;

public class MiscTests {

    @Test
    public void numberTest() throws Exception {
        BigInteger num = new BigInteger("5000");
        System.out.println(num);

        byte[] be = Helpers.biToBigEndian(num);
        String beHex = Helpers.hex(be);

        System.out.println(beHex);

        BigInteger num2 = new BigInteger(be);
        System.out.println(num2);
    }

}
