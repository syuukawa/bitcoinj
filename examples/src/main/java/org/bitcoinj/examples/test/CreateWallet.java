package org.bitcoinj.examples.test;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;

//创建一个钱包
public class CreateWallet {

    public static void main(String[] args) {

        NetworkParameters params = RegTestParams.get();
        Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);

        final File walletFile = new File("regtest-001.wallet");
        try {
            wallet.saveToFile(walletFile);
        } catch (IOException e) {
            System.out.println("Unable to create wallet file.");
        }

        System.out.println("wallet: " + wallet.toString());
        DeterministicSeed seed = wallet.getKeyChainSeed();
        
        System.out.println("seed: " + seed.toString());
        System.out.println("creation time: " + seed.getCreationTimeSeconds());
        System.out.println("mnemonicCode: " + Utils.SPACE_JOINER.join(seed.getMnemonicCode()));
    }
}
