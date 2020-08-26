package io.sonocoin.sonolib.crypto;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

import java.security.SecureRandom;

public final class Mnemonic {

    public final String words;
    private final byte[] seed;

    private void validateWords(String words) throws Exception {
        MnemonicValidator
                .ofWordList(English.INSTANCE)
                .validate(words);
    }

    public Mnemonic(int count) throws Exception {
        Words words;
        switch (count) {
            case 12: words = Words.TWELVE; break;
            case 15: words = Words.FIFTEEN; break;
            case 18: words = Words.EIGHTEEN; break;
            case 21: words = Words.TWENTY_ONE; break;
            case 24: words = Words.TWENTY_FOUR; break;
            default: throw new Exception("Invalid words count");
        }

        StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[words.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, sb::append);

        this.words = sb.toString();
        this.seed = new SeedCalculator().calculateSeed(this.words, "");
    }

    public Mnemonic(String words) {
//        validateWords(words);
        this.words = words;
        this.seed = new SeedCalculator().calculateSeed(this.words, "");
    }

    public HD toHD(int index) throws Exception {
        return new HD(seed, index);
    }

    public Wallet toWallet(int index) throws Exception {
        HD hd = toHD(index);
        return hd.toWallet();
    }

}
