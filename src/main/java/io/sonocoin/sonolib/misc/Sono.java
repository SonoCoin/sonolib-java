package io.sonocoin.sonolib.misc;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Sono {
    public static final BigInteger zero = new BigInteger("0");
    public static final BigInteger commission = new BigInteger("1000000");
    public static final BigInteger currencyDivider = new BigInteger("100000000");
    public static final BigDecimal satoshi = new BigDecimal("100000000");

    public static final int txVersion = 1;
}
