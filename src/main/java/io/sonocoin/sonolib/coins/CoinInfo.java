package io.sonocoin.sonolib.coins;

import java.math.BigInteger;

public class CoinInfo {

    public CoinStatus status;
    public BigInteger amount;

    public CoinInfo(int status, BigInteger amount) throws Exception {
        switch (status) {
            case 1:
                this.status = CoinStatus.Active; break;
            case 2:
                this.status = CoinStatus.Spent; break;
            default: throw new Exception("Invalid coin status: " + status);
        }

        this.amount = amount;
    }

}
