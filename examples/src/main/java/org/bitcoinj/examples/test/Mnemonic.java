package org.bitcoinj.examples.test;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;


//http://www.netkiller.cn/blockchain/ethereum/web3j/web3j.mnemonic.html
public class Mnemonic {


    public static void main(String[] args) throws UnreadableWalletException, IOException {
        // TODO Auto-generated method stub

        String passphrase = "";
        SecureRandom secureRandom = new SecureRandom();
        DeterministicSeed deterministicSeed = new DeterministicSeed(secureRandom, 128, passphrase);
        List<String> mnemonicCode = deterministicSeed.getMnemonicCode();
        System.out.println(String.join(" ", mnemonicCode));
    }

}
