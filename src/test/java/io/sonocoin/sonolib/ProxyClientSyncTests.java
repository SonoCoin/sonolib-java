//package io.sonocoin.sonolib;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.sonocoin.sonolib.client.ProxyClientSync;
//import io.sonocoin.sonolib.client.responses.ContractBalanceDto;
//import io.sonocoin.sonolib.client.responses.ContractCoinDto;
//import io.sonocoin.sonolib.client.responses.ContractDto;
//import io.sonocoin.sonolib.crypto.Mnemonic;
//import io.sonocoin.sonolib.dtos.extended.BalanceExtendedDto;
//import io.sonocoin.sonolib.dtos.extended.HistoryItemDto;
//import io.sonocoin.sonolib.dtos.extended.NetworkDto;
//import io.sonocoin.sonolib.dtos.extended.NonceDto;
//import org.junit.Test;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProxyClientSyncTests {
//
//    private final ObjectMapper mapper;
//    private final static String baseAddr = "https://api.sonocoin.io/proxy/api";
//    private final static String network = "TestNet";
//    private final static String words = "paper rain few enjoy weapon setup chat pigeon bargain title cruise original draft cliff guitar estate robot use update exact brand gorilla snake supreme";
//    private final static int walletIndex = 0;
//
//    private final static String tokenContract = "SXQesoQbc9dfCHnrPzoSt2fGgPppRVcBS7B";
//
//    private final static String address = "SCYzr37bgyLbvwQkfEUVtppcFgGCq4saj7i";
//    private final static String receiver = "SCbGU7U3YusUfpDG7TZ3RyWsTqWyPkHiRsk";
//
//    public final static String coinContract = "";
//
//    private final ProxyClientSync client;
//
//    public ProxyClientSyncTests() {
//        this.client = new ProxyClientSync(baseAddr);
//        this.mapper = new ObjectMapper();
//    }
//
//    private void debug(Object x) throws JsonProcessingException {
//        String json = this.mapper.writeValueAsString(x);
//        System.out.println(json);
//    }
//
//    @Test
//    public void getNetworkTest() {
//        try {
//            List<NetworkDto> networks = client.getNetworks();
//            debug(networks);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void getHistoryTest() {
//        try {
//            List<HistoryItemDto> history = client.getHistory(network, "SCigaYBVmALLd5bvEd3QvWL27FS8nsjssDY");
//            debug(history);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void getBalanceTest() {
//        try {
//            BalanceExtendedDto resp = client.getBalance(network, "SCVEQpm9D4ypMGpkXUoKqY7iNPkYEa8JABk");
//            debug(resp);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void getBalancesTest() {
//        try {
//            List<String> addresses = new ArrayList<>();
//            addresses.add(receiver);
//            List<BalanceExtendedDto> resp = client.getBalances(network, addresses);
//            debug(resp);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void getTokenBalancesTest() {
//        try {
//            List<ContractBalanceDto> resp = client.getTokenBalances(network, address);
//            debug(resp);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void getContractsTest() {
//        try {
//            List<ContractDto> resp = client.getContracts(network);
//            debug(resp);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @Test
//    public void getNonceTest() throws Exception {
//        NonceDto nonce = client.getNonce(network, address);
//        assert nonce.unconfirmedNonce.compareTo(new BigInteger("0")) != 0;
//    }
//
//    @Test
//    public void sendTest() throws Exception {
//        BigDecimal amount = new BigDecimal("0.3");
//
//        assert client.send(network, words, walletIndex, receiver, amount);
//    }
//
//    @Test
//    public void sendTokenTest() throws Exception {
//        String contract = "SXQesoQbc9dfCHnrPzoSt2fGgPppRVcBS7B";
//        BigDecimal amount = new BigDecimal("0.1");
//
//        assert client.sendToken(network, words, walletIndex, contract, receiver, amount);
//    }
//
//
//    // Coins
//
//    @Test
//    public void createCoinTest() throws Exception {
//        BigDecimal amount = new BigDecimal("0.3");
//
//        ContractCoinDto contract = client.getCoinContract(network);
//
//        String coinSecretKey = client.createCoin(network, words, walletIndex, contract.address, amount);
//
//        System.out.println(coinSecretKey);
//    }
//
//    @Test
//    public void coinInfoTest() throws Exception {
//        String coinSecret = "52e395ec0c0fd8ffaf7b590298bd98e3412cb3852e82fa501078f00d29ba07772d62d51b0eabbc00a4d8e7c9e8df59c08c7e77f0f110db03809699df2c9559cd";
//
//        ContractCoinDto contract = client.getCoinContract(network);
//
//        BigDecimal amount = client.getCoinInfo(network, contract.address, coinSecret);
//
//        System.out.println(amount);
//    }
//
//    @Test
//    public void spendCoinTest() throws Exception {
//        String coinSecret = "52e395ec0c0fd8ffaf7b590298bd98e3412cb3852e82fa501078f00d29ba07772d62d51b0eabbc00a4d8e7c9e8df59c08c7e77f0f110db03809699df2c9559cd";
//
//        ContractCoinDto contract = client.getCoinContract(network);
//
//        assert client.spendCoin(network, words, walletIndex, contract.address, coinSecret);
//    }
//
//    // token coins
//
//    @Test
//    public void createTokenCoinTest() throws Exception {
//        BigDecimal amount = new BigDecimal("100.04");
//        String coinSecretKey = client.createTokenCoin(network, words, walletIndex, tokenContract, amount);
//
//        System.out.println(coinSecretKey);
//    }
//
//    @Test
//    public void tokenCoinInfoTest() throws Exception {
//        String coinSecret = "c125747baa98a4efb22e47937ecc7812adf70d40eacc020fa27a55c189d26172c484d8fb1867b0ffaf4c26516ccce473dba71e06ecb7d76212fd986123389bae";
//
//        Mnemonic mnemonic = new Mnemonic(words);
//        String owner = mnemonic.toWallet(walletIndex).base58Address;
//
//        BigDecimal amount = client.getTokenCoinInfo(network, owner, tokenContract, coinSecret);
//        System.out.println(amount);
//    }
//
//    @Test
//    public void spendTokenCoinTest() throws Exception {
//        String coinSecret = "c125747baa98a4efb22e47937ecc7812adf70d40eacc020fa27a55c189d26172c484d8fb1867b0ffaf4c26516ccce473dba71e06ecb7d76212fd986123389bae";
//
//        Mnemonic mnemonic = new Mnemonic(words);
//        String owner = mnemonic.toWallet(walletIndex).base58Address;
//
//        assert client.spendTokenCoin(network, words, walletIndex, owner, tokenContract, coinSecret);
//    }
//
//}
