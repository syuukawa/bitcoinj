/*
 * Copyright 2011 Google Inc.
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.examples.test;

import org.bitcoinj.core.*;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;

import java.io.File;

/**
 * RefreshWallet loads a wallet, then processes the block chain to update the transaction pools within it.
 * RefreshWallet加载一个钱包，然后处理块链来更新其中的事务池。
 * To get a test wallet you can use wallet-tool from the tools subproject.
 * 要获得一个测试钱包，您可以使用tools子项目中的wallet-tool
 */
public class RefreshWallet {
    public static void main(String[] args) throws Exception {

        File file = new File("regtest-send-002.wallet");
        Wallet wallet = Wallet.loadFromFile(file);
        System.out.println(wallet.toString());

        // Set up the components and link them together.
        final NetworkParameters params = RegTestParams.get();
//TODO
        BlockStore blockStore = new MemoryBlockStore(params);
        BlockChain chain = new BlockChain(params, wallet, blockStore);

        final PeerGroup peerGroup = new PeerGroup(params, chain);
        peerGroup.startAsync();

        wallet.addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public synchronized void onCoinsReceived(Wallet w, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("\nReceived tx " + tx.getTxId());
                System.out.println(tx.toString());
            }
        });


        // Now download and process the block chain.
        peerGroup.downloadBlockChain();
        //TODO Add by river
        Thread.sleep(5000);

        peerGroup.stopAsync();
        wallet.saveToFile(file);
        System.out.println("\nDone!\n");
        System.out.println(wallet.toString());
        System.out.println( "Wallet Balance : " + wallet.getBalance()) ;

        Address address = wallet.currentReceiveAddress();
        System.out.println("Wallet address: " + address.toString());


    }
}
