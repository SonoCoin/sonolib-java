package io.sonocoin.sonolib.erc20;

import io.sonocoin.sonolib.crypto.HD;
import io.sonocoin.sonolib.crypto.Wallet;
import io.sonocoin.sonolib.dtos.TransactionRequest;
import io.sonocoin.sonolib.misc.Helpers;
import io.sonocoin.sonolib.misc.Sono;
import io.sonocoin.sonolib.misc.base58.AddressFormatException;
import io.sonocoin.sonolib.misc.base58.Base58;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class Erc20Transfer {

    public final static String transferHex = "5d359fbd";
    public final static String balanceHex = "70a08231";
    public final static String approveHex = "1086a9aa";
    public final static String transferFromHex = "2ea0dfe1";
    public final static String allowanceHex = "dd62ed3e";

    private final TransactionRequest txRequest;
    private String sender;

    public Erc20Transfer() {
        this.txRequest = new TransactionRequest();
    }

    public Erc20Transfer addCommission(BigInteger gasPrice) {
        this.txRequest.addCommission(gasPrice, Sono.zero);
        return this;
    }

    public Erc20Transfer addSender(String address, HD hd, BigInteger amount, BigInteger nonce) {
        this.sender = address;
        this.txRequest.addSender(address, hd, amount, nonce);
        return this;
    }

    public Erc20Transfer addTransfer(String contract, String address, BigInteger amount, BigInteger commission) throws Exception {
//        String addr = Helpers.hex(Base58.decode(address));
//        String am = Helpers.hex(Helpers.biToBigEndian(amount));
        String payload = Erc20Transfer.getTransferPayload(address, amount);
//        String payload = transferHex + addr + am;
        this.txRequest.addContractExecution(this.sender, contract, payload, new BigInteger("0"), commission);
        return this;
    }

    public TransactionRequest sign() throws Exception {
        return this.txRequest.sign();
    }

    public static String getTransferPayload(String address, BigInteger amount) throws Exception {
        String addr = Helpers.hex(Base58.decode(address));
        String am = Helpers.hex(Helpers.biToBigEndian(amount));
        return transferHex + addr + am;
    }

    public static String getBalancePayload(String contract, String address) throws AddressFormatException {
        String addr = Helpers.hex(Base58.decode(address));
        return balanceHex + addr;
    }

    public static String getApprovePayload(byte[] coinPk, BigInteger amount) throws Exception {
        byte[] amountSlice = Helpers.biToBigEndian(amount);

        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(new Wallet(coinPk).address);
        payload.write(amountSlice);

        return approveHex + Helpers.hex(payload.toByteArray());
    }

    public static String getTransferFromPayload(String sender, String receiver, BigInteger amount) throws Exception {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(Base58.decode(sender));
        payload.write(Base58.decode(receiver));
        payload.write(Helpers.biToBigEndian(amount));

        return transferFromHex + Helpers.hex(payload.toByteArray());
    }

    public static String getAllowancePayload(String owner, String spender) throws Exception {
        ByteArrayOutputStream payload = new ByteArrayOutputStream();
        payload.write(Base58.decode(owner));
        payload.write(Base58.decode(spender));

        return allowanceHex + Helpers.hex(payload.toByteArray());
    }

}
