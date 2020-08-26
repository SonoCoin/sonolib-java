package io.sonocoin.sonolib.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.sonocoin.sonolib.client.requests.BalanceRequestDto;
import io.sonocoin.sonolib.client.requests.StaticCallRequestDto;
import io.sonocoin.sonolib.client.responses.ContractBalanceDto;
import io.sonocoin.sonolib.client.responses.ContractCoinDto;
import io.sonocoin.sonolib.client.responses.ContractDto;
import io.sonocoin.sonolib.client.responses.StaticCallDto;
import io.sonocoin.sonolib.coins.Coin;
import io.sonocoin.sonolib.coins.CoinDidSpendException;
import io.sonocoin.sonolib.coins.CoinInfo;
import io.sonocoin.sonolib.coins.CoinStatus;
import io.sonocoin.sonolib.crypto.HD;
import io.sonocoin.sonolib.crypto.Mnemonic;
import io.sonocoin.sonolib.crypto.Wallet;
import io.sonocoin.sonolib.dtos.TransactionRequest;
import io.sonocoin.sonolib.dtos.extended.*;
import io.sonocoin.sonolib.erc20.Erc20Transfer;
import io.sonocoin.sonolib.misc.Payload;
import io.sonocoin.sonolib.misc.Sono;
import jdk.internal.org.jline.reader.History;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class ProxyClientSync {

    public interface ClientResponse<T> {
        public void onSuccess(T networks);
        public void onError(Exception exception);
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String baseAddr;
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final BigInteger gas = new BigInteger("100000000000");
    private final BigInteger fee = new BigInteger("0");
    private final BigInteger gasPrice = new BigInteger("0");
    private final BigInteger commission = new BigInteger("0");

    public ProxyClientSync(String baseAddr) {
        this.client = new OkHttpClient();
        this.baseAddr = baseAddr;
        this.mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<NetworkDto> getNetworks() throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/info/networks")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<List<NetworkDto>>() {});
    }

    // History

    public List<HistoryItemDto> getHistory(String network, String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/history/" + network + "/" + address)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<List<HistoryItemDto>>() {});
    }

    // Balances

    public BalanceExtendedDto getBalance(String network, String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/wallets/" + network + "/" + address + "/balance")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), BalanceExtendedDto.class);
    }

    public List<BalanceExtendedDto> getBalances(String network, List<String> addresses) throws Exception {
        BalanceRequestDto req = new BalanceRequestDto(addresses);
        String json = this.mapper.writeValueAsString(req);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(this.baseAddr + "/node/" + network + "/contract/static_call")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<List<BalanceExtendedDto>>() {});
    }

    public List<ContractBalanceDto> getTokenBalances(String network, String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/contracts/" + network + "/" + address + "/balances")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<List<ContractBalanceDto>>() {});
    }

    // Static call
    public String staticCall(String network, String contract, String payload) throws Exception {
        StaticCallRequestDto req = new StaticCallRequestDto(contract, payload);
        String json = this.mapper.writeValueAsString(req);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(this.baseAddr + "/node/" + network + "/contract/static_call")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }
        StaticCallDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), StaticCallDto.class);
        return res.result;
    }

    // Contracts

    public List<ContractDto> getContracts(String network) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/contracts/" + network)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<List<ContractDto>>() {});
    }

    public ContractDto getContract(String network, String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/contracts/" + network + "/" + address)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ContractDto.class);
    }

    public ContractCoinDto getCoinContract(String network) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/contracts/" + network + "/coin")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ContractCoinDto.class);
    }

    // Send tx
    public NonceDto getNonce(String network, String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/node/" + network + "/account/" + address + "/nonce")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), NonceDto.class);
    }

    public NonceDto getAllowanceNonce(String network, String address) throws Exception {
        Request request = new Request.Builder()
                .url(this.baseAddr + "/node/" + network +  "/wallet/" + address + "/allowance_nonce")
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }

        return this.mapper.readValue(Objects.requireNonNull(response.body()).string(), NonceDto.class);
    }

    public boolean send(String network, TransactionRequest tx) throws Exception {
        String json = this.mapper.writeValueAsString(tx);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(this.baseAddr + "/node/" + network + "/txs/publish")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            ErrorDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), ErrorDto.class);
            throw new Exception(res.errorString);
        }
        TxPublishResponseDto res = this.mapper.readValue(Objects.requireNonNull(response.body()).string(), TxPublishResponseDto.class);

        return res.result.equals("ok");
    }

    public boolean send(String network, String words, int walletIndex, String receiver, BigDecimal amount) throws Exception {
        BigInteger val = amount.multiply(Sono.satoshi).toBigInteger();

        Mnemonic mnemonic = new Mnemonic(words);

        HD hd = mnemonic.toHD(walletIndex);
        String sender = hd.toWallet().base58Address;

        NonceDto nonce = this.getNonce(network, sender);

        TransactionRequest tx = new TransactionRequest()
                .addCommission(gasPrice, gas)
                .addSender(sender, hd, val, nonce.unconfirmedNonce)
                .addTransfer(receiver, val)
                .sign();

        return this.send(network, tx);
    }

    // Send token
    public boolean sendToken(String network, String words, int walletIndex, String contractAddress, String receiver, BigDecimal amount) throws Exception {
        ContractDto contract = getContract(network, contractAddress);

        BigDecimal multiplier = new BigDecimal("10");
        multiplier = multiplier.pow(contract.decimals);

        BigInteger val = amount.multiply(multiplier).toBigInteger();

        Mnemonic mnemonic = new Mnemonic(words);
        HD hd = mnemonic.toHD(walletIndex);
        String sender = mnemonic.toWallet(walletIndex).base58Address;

        BigInteger nonce = this.getNonce(network, sender).unconfirmedNonce;

        TransactionRequest tx = new Erc20Transfer()
                .addCommission(gasPrice)
                .addSender(sender, hd, commission, nonce)
                .addTransfer(contractAddress, receiver, val, gas)
                .sign();

        return this.send(network, tx);
    }

    // Coins

    /**
     * Generate coin
     * @return Coin private key
     */
    public String createCoin(String network, String words, int walletIndex, String contract, BigDecimal amount) throws Exception {
        BigInteger val = amount.multiply(Sono.satoshi).toBigInteger();

        Mnemonic mnemonic = new Mnemonic(words);

        HD hd = mnemonic.toHD(walletIndex);
        String sender = hd.toWallet().base58Address;

        BigInteger nonce = this.getNonce(network, sender).unconfirmedNonce;
        BigInteger allowanceNonce = this.getAllowanceNonce(network, sender).unconfirmedNonce;

        Coin coin = new Coin();

        String payload = Coin.getCreateCoinPayload(hd, coin.publicKeyHex(), val, allowanceNonce);

        TransactionRequest tx = new TransactionRequest()
                .addCommission(gasPrice, gas)
                .addSender(sender, hd, commission, nonce)
                .addContractExecution(sender, contract, payload, Sono.zero, gas)
                .sign();

        this.send(network, tx);
        return coin.secretKeyHex();
    }

    /**
     * get coin info
     * @return Coin private key
     */
    public BigDecimal getCoinInfo(String network, String contract, String coinSecretKey) throws Exception {
        Coin coin = new Coin(coinSecretKey);

        String payload = Coin.getInfoPayload(coin.publicKeyHex());

        String resp = this.staticCall(network, contract, payload);

        CoinInfo coinInfo = Payload.toCoinInfo(resp);
        if (coinInfo.status == CoinStatus.Spent) {
            throw new CoinDidSpendException();
        }
        BigDecimal am = new BigDecimal(coinInfo.amount);
        return am.divide(Sono.satoshi, new MathContext(8, RoundingMode.HALF_EVEN));
    }

    public boolean spendCoin(String network, String words, int walletIndex, String contract, String coinSecretKey) throws Exception {
        Coin coin = new Coin(coinSecretKey);

        Mnemonic mnemonic = new Mnemonic(words);
        HD hd = mnemonic.toHD(walletIndex);
        String sender = hd.toWallet().base58Address;

        BigInteger nonce = this.getNonce(network, sender).unconfirmedNonce;

        String payload = Coin.getSpendCoinPayload(coin.keys, sender);

        TransactionRequest tx = new TransactionRequest()
                .addCommission(gasPrice, gas)
                .addSender(sender, hd, commission, nonce)
                .addContractExecution(sender, contract, payload, Sono.zero, gas)
                .sign();

        return this.send(network, tx);
    }

    // coin token
    public String createTokenCoin(String network, String words, int walletIndex, String contractAddress, BigDecimal amount) throws Exception {
        ContractDto contract = getContract(network, contractAddress);
        BigDecimal multiplier = new BigDecimal("10");
        multiplier = multiplier.pow(contract.decimals);

        BigInteger val = amount.multiply(multiplier).toBigInteger();

        Mnemonic mnemonic = new Mnemonic(words);

        HD hd = mnemonic.toHD(walletIndex);
        String sender = hd.toWallet().base58Address;

        Coin coin = new Coin();
        String payload = Erc20Transfer.getApprovePayload(coin.publicKey(), val);

        BigInteger nonce = this.getNonce(network, sender).unconfirmedNonce;
        TransactionRequest tx = new TransactionRequest()
                .addCommission(gasPrice, gas)
                .addSender(sender, hd, commission, nonce)
                .addContractExecution(sender, contract.address, payload, Sono.zero, gas)
                .sign();

        this.send(network, tx);
        return coin.secretKeyHex();
    }

    private BigInteger _getTokenCoinInfo(String network, String owner, String contractAddress, String coinSecretKey) throws Exception {
        HD hd = new HD(coinSecretKey);
        String spender = hd.toWallet().base58Address;

        String payload = Erc20Transfer.getAllowancePayload(owner, spender);

        String resp = this.staticCall(network, contractAddress, payload);
        return Payload.toBi(resp);
    }

    public BigDecimal getTokenCoinInfo(String network, String owner, String contractAddress, String coinSecretKey) throws Exception {
        ContractDto contract = getContract(network, contractAddress);
        BigDecimal multiplier = new BigDecimal("10");
        multiplier = multiplier.pow(contract.decimals);

        BigInteger balance = this._getTokenCoinInfo(network, owner, contractAddress, coinSecretKey);

        BigDecimal b = new BigDecimal(balance);
        return b.divide(multiplier);
    }

    public boolean spendTokenCoin(String network, String words, int walletIndex, String owner, String contract, String coinSecretKey) throws Exception {
        BigInteger amount = this._getTokenCoinInfo(network, owner, contract, coinSecretKey);

        HD hd = new HD(coinSecretKey);
        String sender = hd.toWallet().base58Address;

        Mnemonic mnemonic = new Mnemonic(words);
        HD hd2 = mnemonic.toHD(walletIndex);
        String receiver = hd2.toWallet().base58Address;

        BigInteger nonce = this.getNonce(network, sender).unconfirmedNonce;

        String payload = Erc20Transfer.getTransferFromPayload(sender, receiver, amount);

        TransactionRequest tx = new TransactionRequest()
                .addCommission(gasPrice, gas)
                .addSender(sender, hd, commission, nonce)
                .addContractExecution(sender, contract, payload, Sono.zero, gas)
                .sign();
        return this.send(network, tx);
    }


}
