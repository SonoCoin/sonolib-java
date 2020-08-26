package io.sonocoin.sonolib.dtos;

import io.sonocoin.sonolib.misc.base58.Base58;
import org.apache.commons.codec.binary.Hex;
import io.sonocoin.sonolib.misc.Helpers;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class ContractMessageDto {

    public String sender;
    public String address;
    public String payload;
    public BigInteger value;
    public BigInteger gas;

    public ContractMessageDto() {
        super();
    }

    public ContractMessageDto(String sender, String code, BigInteger value, BigInteger gas) {
        this.sender = sender;
        this.payload = code;
        this.value = value;
        this.gas = gas;
    }

    public ContractMessageDto(String sender, String address, String payload, BigInteger value, BigInteger gas) {
        this.sender = sender;
        this.address = address;
        this.payload = payload;
        this.value = value;
        this.gas = gas;
    }

    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        buf.write(Base58.decode(sender));
        buf.write(Hex.decodeHex(this.payload.toCharArray()));
        buf.write(Helpers.biToLittleEndian(this.value));
        buf.write(Helpers.biToLittleEndian(this.gas));

        if (this.address != null && !this.address.isEmpty()) {
            buf.write(Base58.decode(this.address));
        }

        return buf.toByteArray();
    }

}
