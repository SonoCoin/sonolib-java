package io.sonocoin.sonolib.dtos;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

public class StakeDto extends TransferDto {
    public String nodeId;

    public StakeDto() {
        super();
    }

    public StakeDto(String address, BigInteger value, String nodeId) {
        this.address = address;
        this.value = value;
        this.nodeId = nodeId;
    }

    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(super.toBytes());

        if (this.nodeId != null && !this.nodeId.isEmpty()) {
            buf.write(Hex.decodeHex(this.nodeId.toCharArray()));
        }

        return buf.toByteArray();
    }
}
