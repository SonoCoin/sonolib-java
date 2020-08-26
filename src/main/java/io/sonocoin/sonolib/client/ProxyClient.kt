package io.sonocoin.sonolib.client

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import io.sonocoin.sonolib.client.requests.StaticCallRequestDto
import io.sonocoin.sonolib.client.responses.*
import io.sonocoin.sonolib.coins.Coin
import io.sonocoin.sonolib.coins.CoinDidSpendException
import io.sonocoin.sonolib.coins.CoinStatus
import io.sonocoin.sonolib.crypto.HD
import io.sonocoin.sonolib.crypto.Mnemonic
import io.sonocoin.sonolib.dtos.ContractMessageDto
import io.sonocoin.sonolib.dtos.TransactionRequest
import io.sonocoin.sonolib.dtos.extended.*
import io.sonocoin.sonolib.erc20.Erc20Transfer
import io.sonocoin.sonolib.misc.Payload
import io.sonocoin.sonolib.misc.Sono
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode


class ProxyClient(val baseAddr: String) {

    private val gas = BigInteger("100000000000")
    private val fee = BigInteger("0")
    private val gasPrice = BigInteger("0")
    private val commission = BigInteger("0")

    private var client: OkHttpClient = OkHttpClient()
    private var mapper: ObjectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private fun<T> call(url: String, params: T, onSuccess: (String) -> Unit, onError: (IOException) -> Unit) {
        val reqJson = mapper.writeValueAsString(params)

        val builder = Request.Builder()
                .url(url)

        if (params != null) {
            builder.post(reqJson.toRequestBody(mediaType))
        }

        val request = builder.build()

        val response = client.newCall(request)
        response.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body == null) {
                    onError(IOException("Response is null"))
                    return
                }
                val json = response.body?.string()
                if (json == null) {
                    onError(IOException("Response is null"))
                    return
                }

                if (!response.isSuccessful) {
                    try {
                        val res = mapper.readValue(json, ErrorDto::class.java)
                        onError(IOException(res.errorString))
                    } catch (e: Exception) {
                        onError(IOException(response.code.toString()))
                    }
                    return
                }
                onSuccess(json)
            }
        })
    }

    private fun get(url: String, onSuccess: (String) -> Unit, onError: (IOException) -> Unit) {
        call(url, null, onSuccess, onError)
    }

    private fun<T> post(url: String, params: T, onSuccess: (String) -> Unit, onError: (IOException) -> Unit) {
        call(url, params, onSuccess, onError)
    }

    fun getNetworks(onSuccess: (List<NetworkDto>) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/info/networks", { json ->
            val res = mapper.readValue(json, object : TypeReference<List<NetworkDto>>() {})
            onSuccess(res)
        }, onError)
    }

    // History
    fun getHistory(network: String, address: String, onSuccess: (List<HistoryItemDto>) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/history/$network/$address", { json ->
            val res = mapper.readValue(json, object : TypeReference<List<HistoryItemDto>>() {})
            onSuccess(res)
        }, onError)
    }

    // History
    fun getHistorySamples(network: String, address: String, onSuccess: (List<HistoryItemDto>) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/history/$network/$address/sample", { json ->
            val res = mapper.readValue(json, object : TypeReference<List<HistoryItemDto>>() {})
            onSuccess(res)
        }, onError)
    }

    // Balances
    fun getBalance(network: String, address: String, onSuccess: (BalanceExtendedDto) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/wallets/$network/$address/balance", { json ->
            val res = mapper.readValue(json, BalanceExtendedDto::class.java)
            onSuccess(res)
        }, onError)
    }

    fun getTokenBalances(network: String, address: String, onSuccess: (List<ContractBalanceDto>) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/contracts/$network/$address/balances", { json ->
            val res = mapper.readValue(json, object : TypeReference<List<ContractBalanceDto>>() {})
            onSuccess(res)
        }, onError)
    }

    // Static call
    fun staticCall(network: String, contract: String, payload: String, onSuccess: (String) -> Unit, onError: (IOException) -> Unit) {
        val req = StaticCallRequestDto(contract, payload)

        post("$baseAddr/node/$network/contract/static_call", req, { json ->
            val res = mapper.readValue(json, StaticCallDto::class.java)
            onSuccess(res.result)
        }, onError)
    }

    // consumed fee
    fun consumedFee(network: String, sender: String, contract: String, payload: String, onSuccess: (BigInteger) -> Unit, onError: (IOException) -> Unit) {
        consumedFee(network, sender, contract, payload, Sono.zero, Sono.zero, onSuccess, onError)
    }

    fun consumedFee(network: String, sender: String, contract: String, payload: String, value: BigInteger, commission: BigInteger, onSuccess: (BigInteger) -> Unit, onError: (IOException) -> Unit) {
        val req = ContractMessageDto(sender,  contract, payload, value, commission)

        post("$baseAddr/node/$network/contract/consumed_fee", req, { json ->
            val res = mapper.readValue(json, ConsumedFeeDto::class.java)
            onSuccess(res.consumedFee)
        }, onError)
    }

    // Contracts
    fun getContracts(network: String, onSuccess: (List<ContractDto>) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/contracts/$network", { json ->
            val res = mapper.readValue(json, object : TypeReference<List<ContractDto>>() {})
            onSuccess(res)
        }, onError)
    }

    fun getContract(network: String, address: String, onSuccess: (ContractDto) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/contracts/$network/$address", { json ->
            val res = mapper.readValue(json, ContractDto::class.java)
            onSuccess(res)
        }, onError)
    }

    fun getCoinContract(network: String, onSuccess: (ContractCoinDto) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/contracts/$network/coin", { json ->
            val res = mapper.readValue(json, ContractCoinDto::class.java)
            onSuccess(res)
        }, onError)
    }

    // send tx
    fun getNonce(network: String, address: String, onSuccess: (NonceDto) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/node/$network/account/$address/nonce", { json ->
            val res = mapper.readValue(json, NonceDto::class.java)
            onSuccess(res)
        }, onError)
    }

    fun getAllowanceNonce(network: String, address: String, onSuccess: (NonceDto) -> Unit, onError: (IOException) -> Unit) {
        get("$baseAddr/node/$network/wallet/$address/allowance_nonce", { json ->
            val res = mapper.readValue(json, NonceDto::class.java)
            onSuccess(res)
        }, onError)
    }

    fun send(network: String, tx: TransactionRequest, onSuccess: (Boolean) -> Unit, onError: (IOException) -> Unit) {
        post("$baseAddr/node/$network/txs/publish", tx, { json ->
            val res = mapper.readValue(json, TxPublishResponseDto::class.java)
            onSuccess(res.result == "ok")
        }, onError)
    }

    fun send(network: String, words: String, walletIndex: Int, receiver: String, amount: BigDecimal, onSuccess: (Boolean) -> Unit, onError: (IOException) -> Unit) {
        val txAmount = amount.multiply(Sono.satoshi).toBigInteger()

        val mnemonic = Mnemonic(words)

        val hd = mnemonic.toHD(walletIndex)
        val sender = hd.toWallet().base58Address

        getNonce(network, sender, { nonce ->
            val tx = TransactionRequest()
                    .addCommission(gasPrice, gas)
                    .addSender(sender, hd, txAmount, nonce.unconfirmedNonce)
                    .addTransfer(receiver, txAmount)
                    .sign()

            send(network, tx, onSuccess, onError)
        }, onError)
    }

    fun sendToken(network: String, words: String, walletIndex: Int, contractAddress: String, receiver: String,
                  amount: BigDecimal, onSuccess: (Boolean) -> Unit, onError: (IOException) -> Unit) {
        getContract(network, contractAddress, { contract ->
            var multiplier = BigDecimal("10")
            multiplier = multiplier.pow(contract.decimals)

            val txAmount = amount.multiply(multiplier).toBigInteger()

            val mnemonic = Mnemonic(words)
            val hd = mnemonic.toHD(walletIndex)
            val sender = mnemonic.toWallet(walletIndex).base58Address


            getNonce(network, sender, { nonce ->
                val payload = Erc20Transfer.getTransferPayload(receiver, txAmount)
                consumedFee(network, sender, contractAddress, payload, { consumedFee ->

                    val tx = Erc20Transfer()
                            .addCommission(gasPrice)
                            .addSender(sender, hd, commission, nonce.unconfirmedNonce)
                            .addTransfer(contractAddress, receiver, txAmount, consumedFee)
                            .sign()

                    send(network, tx, onSuccess, onError)
                }, onError)
            }, onError)
        }, onError)
    }

    // coins
    fun createCoin(network: String, words: String, walletIndex: Int, contract: String, amount: BigDecimal,
                  onSuccess: (String) -> Unit, onError: (IOException) -> Unit) {
        val txAmount = amount.multiply(Sono.satoshi).toBigInteger()
        val mnemonic = Mnemonic(words)
        val hd = mnemonic.toHD(walletIndex)
        val sender = hd.toWallet().base58Address


        getAllowanceNonce(network, sender, { allowanceNonce ->
            val coin = Coin()
            val payload = Coin.getCreateCoinPayload(hd, coin.publicKeyHex(), txAmount, allowanceNonce.unconfirmedNonce)

            getNonce(network, sender, { nonce ->
                consumedFee(network, sender, contract, payload, { consumedFee ->
                    val tx = TransactionRequest()
                            .addCommission(gasPrice, gas)
                            .addSender(sender, hd, commission, nonce.unconfirmedNonce)
                            .addContractExecution(sender, contract, payload, Sono.zero, consumedFee)
                            .sign()

                    send(network, tx, {
                        onSuccess(coin.secretKeyHex())
                    }, onError)
                }, onError)
            }, onError)
        }, onError)
    }

    fun getCoinInfo(network: String, contract: String, coinSecretKey: String,
                  onSuccess: (BigDecimal) -> Unit, onError: (IOException) -> Unit) {
        val coin = Coin(coinSecretKey)
        val payload = Coin.getInfoPayload(coin.publicKeyHex())

        staticCall(network, contract, payload, { resp ->
            val coinInfo = Payload.toCoinInfo(resp)
            if (coinInfo.status == CoinStatus.Spent) {
                throw CoinDidSpendException()
            }

            val am = BigDecimal(coinInfo.amount)
            val res = am.divide(Sono.satoshi, MathContext(8, RoundingMode.HALF_EVEN))
            onSuccess(res)
        }, onError)
    }

    fun spendCoin(network: String, words: String, walletIndex: Int, contract: String, coinSecretKey: String,
                   onSuccess: (Boolean) -> Unit, onError: (IOException) -> Unit) {
        val coin = Coin(coinSecretKey)
        val mnemonic = Mnemonic(words)
        val hd = mnemonic.toHD(walletIndex)
        val sender = hd.toWallet().base58Address

        val payload = Coin.getSpendCoinPayload(coin.keys, sender)

        getNonce(network, sender, { nonce ->
            consumedFee(network, sender, contract, payload, { consumedFee ->
                val tx = TransactionRequest()
                        .addCommission(gasPrice, gas)
                        .addSender(sender, hd, commission, nonce.unconfirmedNonce)
                        .addContractExecution(sender, contract, payload, Sono.zero, consumedFee)
                        .sign()

                send(network, tx, onSuccess, onError)
            }, onError)
        }, onError)
    }

    // coin token
    fun createTokenCoin(network: String, words: String, walletIndex: Int, contractAddress: String, amount: BigDecimal,
                   onSuccess: (String) -> Unit, onError: (IOException) -> Unit) {
       getContract(network, contractAddress, { contract ->
           var multiplier = BigDecimal("10")
           multiplier = multiplier.pow(contract.decimals)

           val txAmount = amount.multiply(multiplier).toBigInteger()

           val mnemonic = Mnemonic(words)

           val hd = mnemonic.toHD(walletIndex)
           val sender = hd.toWallet().base58Address

           val coin = Coin()
           val payload = Erc20Transfer.getApprovePayload(coin.publicKey(), txAmount)

           getNonce(network, sender, { nonce ->
               consumedFee(network, sender, contract.address, payload, { consumedFee ->
                   val tx = TransactionRequest()
                           .addCommission(gasPrice, gas)
                           .addSender(sender, hd, commission, nonce.unconfirmedNonce)
                           .addContractExecution(sender, contract.address, payload, Sono.zero, consumedFee)
                           .sign()

                   send(network, tx, {
                       onSuccess(coin.secretKeyHex())
                   }, onError)
               }, onError)
           }, onError)
       }, onError)
    }

    private fun _getTokenCoinInfo(network: String, owner: String, contractAddress: String, coinSecretKey: String,
                                  onSuccess: (BigInteger) -> Unit, onError: (IOException) -> Unit) {
        val hd = HD(coinSecretKey)
        val spender = hd.toWallet().base58Address

        val payload = Erc20Transfer.getAllowancePayload(owner, spender)

        staticCall(network, contractAddress, payload, { resp ->
            onSuccess(Payload.toBi(resp))
        }, onError)
    }

    fun getTokenCoinInfo(network: String, owner: String, contractAddress: String, coinSecretKey: String,
                                  onSuccess: (BigDecimal) -> Unit, onError: (IOException) -> Unit) {
        getContract(network, contractAddress, { contract ->
            var multiplier = BigDecimal("10")
            multiplier = multiplier.pow(contract.decimals)

            _getTokenCoinInfo(network, owner, contractAddress, coinSecretKey, { balance ->
                val b = BigDecimal(balance)
                val res = b.divide(multiplier)
                onSuccess(res)
            }, onError)
        }, onError)
    }

    fun spendTokenCoin(network: String, words: String, walletIndex: Int, owner: String, contract: String, coinSecretKey: String,
                  onSuccess: (Boolean) -> Unit, onError: (IOException) -> Unit) {
        _getTokenCoinInfo(network, owner, contract, coinSecretKey, { amount ->
            val hd = HD(coinSecretKey)
            val sender = hd.toWallet().base58Address

            val mnemonic = Mnemonic(words)
            val hd2 = mnemonic.toHD(walletIndex)
            val receiver = hd2.toWallet().base58Address

            getNonce(network, sender, { nonce ->
                val payload = Erc20Transfer.getTransferFromPayload(sender, receiver, amount)

                consumedFee(network, sender, contract, payload, { consumedFee ->
                    val tx = TransactionRequest()
                            .addCommission(gasPrice, gas)
                            .addSender(sender, hd, commission, nonce.unconfirmedNonce)
                            .addContractExecution(sender, contract, payload, Sono.zero, consumedFee)
                            .sign()

                    send(network, tx, onSuccess, onError)
                }, onError)
            }, onError)
        }, onError)
    }

}

































