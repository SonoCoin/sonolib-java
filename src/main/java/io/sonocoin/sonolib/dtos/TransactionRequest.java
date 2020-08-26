package io.sonocoin.sonolib.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sonocoin.sonolib.misc.Sono;
import io.sonocoin.sonolib.misc.Helpers;
import io.sonocoin.sonolib.crypto.Crypto;
import io.sonocoin.sonolib.crypto.HD;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionRequest {

    public String hash;
    public TransactionType type;
    public int version;
    public List<TransactionInputDto> inputs;
    public List<TransferDto> transfers;
    public List<ContractMessageDto> messages;
    public List<StakeDto> stakes;
    public BigInteger gasPrice;

    @JsonIgnore
    Map<String, HD> signers;
    @JsonIgnore
    BigInteger transferCommission;

    public TransactionRequest() {
        version = Sono.txVersion;
        inputs = new ArrayList<>();
        signers = new HashMap<>();
        type = TransactionType.Account;
    }

    public TransactionRequest addCommission(BigInteger gasPrice, BigInteger transferCommission) {
        this.gasPrice = gasPrice;
        this.transferCommission = transferCommission;
        return this;
    }

    public byte[] generateHash() throws Exception {
        byte[] buf = this.toBytes();
        return Helpers.DHASH(buf);
    }

    public byte[] toBytes() throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        // Step 1 add Type (4 bytes)
        buf.write(Helpers.intToLittleEndian(type.getValue()));

        // Step 2: add Version (4 bytes)
        buf.write(Helpers.intToLittleEndian(version));

        // Step 3: add gas price (8 bytes)
        buf.write(Helpers.biToLittleEndian(this.gasPrice));

        // Step 4: add Inputs
        if (inputs != null) {
            for (TransactionInputDto item : inputs) {
                buf.write(item.toBytes());
            }
        }

        // Step 5: add Transfers
        if (transfers != null) {
            for (TransferDto item : transfers) {
                buf.write(item.toBytes());
            }
        }

        // Step 6: add Messages
        if (messages != null) {
            for (ContractMessageDto item : messages) {
                buf.write(item.toBytes());
            }
        }

        // Step 7: add Stakes
        if (stakes != null) {
            for (StakeDto item : stakes) {
                buf.write(item.toBytes());
            }
        }

        return buf.toByteArray();
    }

    public void validateValue(BigInteger commission) throws Exception {
        BigInteger len = new BigInteger("0");
        if (this.transfers != null) {
            len = len.add(BigInteger.valueOf(this.transfers.size()));
        }
        if (this.stakes != null) {
            len = len.add(BigInteger.valueOf(this.stakes.size()));
        }

        BigInteger outValue = commission.multiply(this.gasPrice).multiply(len);
        if (transfers != null) {
            for (TransferDto item : transfers) {
                outValue = outValue.add(item.value);
            }
        }
        if (stakes != null) {
            for (StakeDto item : stakes) {
                outValue = outValue.add(item.value);
            }
        }
        if (messages != null) {
            for (ContractMessageDto item : messages) {
                outValue = outValue.add(item.value).add(item.gas.multiply(this.gasPrice));
            }
        }

        BigInteger inValue = new BigInteger("0");
        if (inputs != null) {
            for (TransactionInputDto item : inputs) {
                inValue = inValue.add(item.value);
            }
        }

        if (!inValue.equals(outValue)) {
            throw new Exception("Wrong sum in transaction, inValue: " + inValue + ", outValue: " + outValue);
        }
    }

    public TransactionRequest addSender(String address, HD key, BigInteger value, BigInteger nonce) {
        inputs.add(new TransactionInputDto(address, value, nonce, key.publicKeyHex()));
        signers.put(address, key);
        return this;
    }

    public TransactionRequest addTransfer(String address, BigInteger value) {
        if (transfers == null) {
            transfers = new ArrayList<>();
        }
        transfers.add(new TransferDto(address, value));
        return this;
    }

    private void checkContactsData() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
    }

    public TransactionRequest addContractCreation(String sender, String code, BigInteger value, BigInteger gas) {
        checkContactsData();
        messages.add(new ContractMessageDto(sender, code, value, gas));
        return this;
    }

    public TransactionRequest addContractExecution(String sender, String address, String input, BigInteger value, BigInteger gas) {
        checkContactsData();
        messages.add(new ContractMessageDto(sender, address, input, value, gas));
        return this;
    }

    public TransactionRequest addStake(String address, BigInteger value, String nodeId) {
        if (stakes == null) {
            stakes = new ArrayList<>();
        }
        stakes.add(new StakeDto(address, value, nodeId));
        return this;
    }

    public void validate() throws Exception {
        validateValue(this.transferCommission);
    }

    public TransactionRequest sign() throws Exception {
        validate();
        for (TransactionInputDto input : inputs) {
            byte[] msg = msgForSignUser(input);
            HD hd = signers.get(input.address);
            byte[] sig = Crypto.sign(hd.cryptoKeys(), msg);
            input.sign = Helpers.hex(sig);
        }

        byte[] hash = generateHash();
        this.hash = Helpers.hex(hash);

        return this;
    }

    private byte[] msgForSignUser(TransactionInputDto input) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        // Step 1 add Type (4 bytes)
        buf.write(Helpers.intToLittleEndian(type.getValue()));

        // Step 2: add Version (4 bytes)
        buf.write(Helpers.intToLittleEndian(version));

        // Step 3: add Version (8 bytes)
        buf.write(Helpers.biToLittleEndian(this.gasPrice));

        // Step 4: add input
        buf.write(input.toBytes());

        // Step 5: add Transfers
        if (transfers != null) {
            for (TransferDto item : transfers) {
                buf.write(item.toBytes());
            }
        }

        // Step 6: add Messages
        if (messages != null) {
            for (ContractMessageDto item : messages) {
                buf.write(item.toBytes());
            }
        }

        // Step 7: add Stakes
        if (stakes != null) {
            for (StakeDto item : stakes) {
                buf.write(item.toBytes());
            }
        }

        return buf.toByteArray();
    }

}



























