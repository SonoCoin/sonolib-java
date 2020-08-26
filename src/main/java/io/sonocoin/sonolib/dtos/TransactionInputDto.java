package io.sonocoin.sonolib.dtos;

import io.sonocoin.sonolib.misc.base58.Base58;
import org.apache.commons.codec.binary.Hex;
import io.sonocoin.sonolib.misc.Helpers;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class TransactionInputDto {

    public String address;
    public BigInteger nonce;
    public String sign;
    public String publicKey;
    public BigInteger value;

    public TransactionInputDto() {
        super();
    }

    public TransactionInputDto(String address, BigInteger value, BigInteger nonce, String publicKey) {
        this.address = address;
        this.nonce = nonce;
        this.publicKey = publicKey;
        this.value = value;
    }

    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        buf.write(Base58.decode(address));
        buf.write(Helpers.biToLittleEndian(this.value));
        buf.write(Helpers.biToLittleEndian(this.nonce));


        if (this.sign != null && !this.sign.isEmpty() &&
                this.publicKey != null && !this.publicKey.isEmpty()) {
            buf.write(Hex.decodeHex(this.sign.toCharArray()));
            buf.write(Hex.decodeHex(this.publicKey.toCharArray()));
        }

        return buf.toByteArray();
    }

}
