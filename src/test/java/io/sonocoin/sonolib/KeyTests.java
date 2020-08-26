package io.sonocoin.sonolib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sonocoin.sonolib.client.Client;
import org.junit.Test;
import io.sonocoin.sonolib.misc.Sono;
import io.sonocoin.sonolib.crypto.HD;
import io.sonocoin.sonolib.crypto.Wallet;
import io.sonocoin.sonolib.dtos.TransactionRequest;
import io.sonocoin.sonolib.dtos.extended.BalanceDto;
import io.sonocoin.sonolib.dtos.extended.NonceDto;

import java.math.BigInteger;

public class KeyTests {

    private final String baseAddr = "https://testnet.sonocoin.io/api/rest/v1";
    private final Client client = new Client(baseAddr);
    private final ObjectMapper mapper = new ObjectMapper();


    private void debug(Object x) throws JsonProcessingException {
        String json = this.mapper.writeValueAsString(x);
        System.out.println(json);
    }

    @Test
    public void balanceTest() {
        String address = "SCfCPbtKBkj4CNP3D2ywNRTJV2jWU6ubdEz";

        try {
            BalanceDto balance = client.getBalance(address);
            debug(balance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void txTest() throws Exception {
        String receiver = "SCfCPbtKBkj4CNP3D2ywNRTJV2jWU6ubdEz";

        String privateKey = "";

        BigInteger amount = new BigInteger("100");

        HD hd = new HD(privateKey);
        Wallet wallet = hd.toWallet();

        BalanceDto balance = client.getBalance(wallet.base58Address);
        debug(balance);

        NonceDto nonce = client.getNonce(wallet.base58Address);

        BigInteger txAmount = amount.multiply(Sono.currencyDivider);

        try {
            TransactionRequest tx = new TransactionRequest()
                    .addSender(wallet.base58Address, hd, txAmount.add(Sono.commission), nonce.unconfirmedNonce)
                    .addTransfer(receiver, txAmount)
                    .sign();

            assert client.send(tx);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
