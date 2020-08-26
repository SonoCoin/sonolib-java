package io.sonocoin.sonolib;

import io.sonocoin.sonolib.crypto.HD;
import io.sonocoin.sonolib.crypto.Mnemonic;
import org.junit.Test;
import io.sonocoin.sonolib.crypto.Wallet;

public class WalletTests {

    @Test
    public void addressTest() {
        String invalid = "SC4677676767676767676766767565656656";
        assert !Wallet.isValidAddress(invalid);

        String valid = "SCjRdq6w3QX8HWusFc6mUXbkMgbKB9shhZt";
        assert Wallet.isValidAddress(valid);
    }

    @Test
    public void GenerateMnemonicTest() throws Exception {
        Mnemonic mnemonic = new Mnemonic(24);
        System.out.println(mnemonic.words);
    }

    @Test
    public void MnemonicTest() throws Exception {
        String words = "fence attend coil impact hunt cloth split sword hip typical nerve mail dutch rack senior egg march endorse";

        try {
            Mnemonic mnemonic1 = new Mnemonic(words);
            HD hd = mnemonic1.toHD(0);
            Wallet wallet = mnemonic1.toWallet(0);

            assert wallet.base58Address.equals("SCfCPbtKBkj4CNP3D2ywNRTJV2jWU6ubdEz");

            String address = mnemonic1.toWallet(1).base58Address;
            System.out.println(address);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
