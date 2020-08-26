package io.sonocoin.sonolib

import io.sonocoin.sonolib.client.ProxyClient
import org.junit.Test

class ProxyClientTest {

    private val client: ProxyClient = ProxyClient("https://api.sonocoin.io/proxy/api")

    private val network = "TestNet"
    private val words = "paper rain few enjoy weapon setup chat pigeon bargain title cruise original draft cliff guitar estate robot use update exact brand gorilla snake supreme"
    private val walletIndex = 0

    private val tokenContract = "SXQesoQbc9dfCHnrPzoSt2fGgPppRVcBS7B"

    private val address = "SCYzr37bgyLbvwQkfEUVtppcFgGCq4saj7i"
    private val receiver = "SCbGU7U3YusUfpDG7TZ3RyWsTqWyPkHiRsk"


    @Test
    fun `get networks`() {
        client.getHistorySamples("asd", "asdasd", { items ->
            print(items)
        }, { er ->
            print(er)
        })

//        whenever(client.getNetworks()).thenAnswer{
//            (it.arguments[0] as Callback).onSuccess()
//        }
//        client.getNetworks({ networks ->
//            println(networks)
//        }, { err ->
//            println(err)
//        })
    }

}