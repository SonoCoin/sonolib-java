//package io.sonocoin.sonolib.client;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.sonocoin.sonolib.client.requests.StaticCallRequestDto;
//import io.sonocoin.sonolib.client.responses.ConsumedFeeDto;
//import io.sonocoin.sonolib.client.responses.StaticCallDto;
//import io.sonocoin.sonolib.dtos.ContractMessageDto;
//import io.sonocoin.sonolib.misc.Sono;
//import kong.unirest.HttpResponse;
//import kong.unirest.Unirest;
//import io.sonocoin.sonolib.dtos.TransactionRequest;
//import io.sonocoin.sonolib.dtos.extended.BalanceDto;
//import io.sonocoin.sonolib.dtos.extended.NonceDto;
//import io.sonocoin.sonolib.dtos.extended.TxPublishResponseDto;
//import org.apache.commons.codec.binary.Hex;
//
//import java.math.BigInteger;
//
//public class ClientOld {
//
//    private final String baseAddr;
//    private final ObjectMapper mapper;
//
//    public ClientOld(String baseAddr) {
//        this.baseAddr = baseAddr;
//        this.mapper = new ObjectMapper()
//                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//    }
//
//    public BalanceDto getBalance(String address) throws JsonProcessingException {
//        HttpResponse<String> request = Unirest.get(this.baseAddr + "/account/" + address + "/balance").asString();
//        return this.mapper.readValue(request.getBody(), BalanceDto.class);
//    }
//
//    public NonceDto getNonce(String address) throws JsonProcessingException {
//        HttpResponse<String> request = Unirest.get(this.baseAddr + "/account/" + address + "/nonce").asString();
//        return this.mapper.readValue(request.getBody(), NonceDto.class);
//    }
//
//    public boolean send(TransactionRequest tx) throws Exception {
//        HttpResponse<String> request = Unirest.post(this.baseAddr + "/txs/publish")
//                .body(this.mapper.writeValueAsString(tx))
//                .asString();
//
//        if (request.getStatus() == 400) {
//            ErrorDto res = this.mapper.readValue(request.getBody(), ErrorDto.class);
//            throw new Exception(res.message);
//        }
//
//        TxPublishResponseDto res = this.mapper.readValue(request.getBody(), TxPublishResponseDto.class);
//        return res.result.equals("ok");
//    }
//
//    public BigInteger consumedFee(String sender, String contract, String payload) throws Exception {
//        return consumedFee(sender, contract, payload, Sono.zero, Sono.zero);
//    }
//
//    public BigInteger consumedFee(String sender, String contract, String payload, BigInteger value, BigInteger commission) throws Exception {
//        ContractMessageDto msg = new ContractMessageDto(sender, contract, payload, value, commission);
//
//        HttpResponse<String> request = Unirest.post(this.baseAddr + "/contract/consumed_fee")
//                .body(this.mapper.writeValueAsString(msg))
//                .asString();
//
//        if (request.getStatus() == 400) {
//            ErrorDto res = this.mapper.readValue(request.getBody(), ErrorDto.class);
//            throw new Exception(res.message);
//        }
//
//        ConsumedFeeDto res = this.mapper.readValue(request.getBody(), ConsumedFeeDto.class);
//        return res.consumedFee;
//    }
//
//    public String staticCall(String contract, String payload) throws Exception {
//        StaticCallRequestDto req = new StaticCallRequestDto(contract, payload);
//
//        HttpResponse<String> request = Unirest.post(this.baseAddr + "/contract/static_call")
//                .body(this.mapper.writeValueAsString(req))
//                .asString();
//
//        if (request.getStatus() == 400) {
//            ErrorDto res = this.mapper.readValue(request.getBody(), ErrorDto.class);
//            throw new Exception(res.message);
//        }
//
//        StaticCallDto res = this.mapper.readValue(request.getBody(), StaticCallDto.class);
//        return res.result;
//    }
//
//}
