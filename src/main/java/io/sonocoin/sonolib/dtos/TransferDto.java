package io.sonocoin.sonolib.dtos;

import io.sonocoin.sonolib.misc.Helpers;
import io.sonocoin.sonolib.misc.base58.Base58;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class TransferDto {

    public String address;

    public BigInteger value;

    public TransferDto() {
        super();
    }

    public TransferDto(String address, BigInteger value) {
        this.address = address;
        this.value = value;
    }

    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        buf.write(Base58.decode(address));
        buf.write(Helpers.biToLittleEndian(this.value));

        return buf.toByteArray();
    }

}
