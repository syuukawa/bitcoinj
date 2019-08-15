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
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.wallet.Wallet;

import java.math.BigInteger;
import java.net.InetAddress;

/**
 * This example shows how to solve the challenge Hal posted here:<p>
 *
 * <a href="http://www.bitcoin.org/smf/index.php?topic=3638.0">http://www.bitcoin.org/smf/index.php?topic=3638
 * .0</a><p>
 *
 * in which a private key with some coins associated with it is published. The goal is to import the private key,
 * claim the coins and then send them to a different address.
 */
public class PrivateKeys {
    public static void main(String[] args) throws Exception {
        // TODO: Assumes main network not testnet. Make it selectable.
//TODO-000
//        NetworkParameters params = MainNetParams.get();
        NetworkParameters params = RegTestParams.get();
        try {
            // Decode the private key from Satoshis Base58 variant. If 51 characters long then it's from Bitcoins
            // dumpprivkey command and includes a version byte and checksum, or if 52 characters long then it has
            // compressed pub key. Otherwise assume it's a raw key.
            ECKey key;
//TODO-001
//            if (args[0].length() == 51 || args[0].length() == 52) {
//                DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, args[0]);
//                key = dumpedPrivateKey.getKey();
//            } else {
//                BigInteger privKey = Base58.decodeToBigInteger(args[0]);
//                key = ECKey.fromPrivate(privKey);
//            }
            String privateKey = "cTWTW1kBgAY8vxCNxz8rYZ54vvkUzbFDQGKZwExVgbPi5NNyNq7h";
            if (privateKey.length() == 51 || privateKey.length() == 52) {
                DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKey);
                key = dumpedPrivateKey.getKey();
            } else {
                BigInteger privKey = Base58.decodeToBigInteger(privateKey);
                key = ECKey.fromPrivate(privKey);
            }

            System.out.println("Address from private key is: " + LegacyAddress.fromKey(params, key).toString());
//TODO-002
// And the address ...
//            Address destination = LegacyAddress.fromBase58(params, args[1]);
            Address destination = LegacyAddress.fromBase58(params, "mjFV5huR6ArRzzMP7ZPMYsEL5pQ54ULxKQ");

            // Import the private key to a fresh wallet.
            Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
            wallet.importKey(key);

            // Find the transactions that involve those coins.
            final MemoryBlockStore blockStore = new MemoryBlockStore(params);
            BlockChain chain = new BlockChain(params, wallet, blockStore);

            final PeerGroup peerGroup = new PeerGroup(params, chain);
            peerGroup.addAddress(new PeerAddress(params, InetAddress.getLocalHost()));
            peerGroup.startAsync();
            peerGroup.downloadBlockChain();

            // And take them!
            System.out.println("Claiming " + wallet.getBalance().toFriendlyString());
//TODO-003
//          wallet.sendCoins(peerGroup, destination, wallet.getBalance());
            Coin value = Coin.parseCoin("0.8");
            wallet.sendCoins(peerGroup, destination, value);


            // Wait a few seconds to let the packets flush out to the network (ugly).
            Thread.sleep(5000);
            peerGroup.stopAsync();
            System.exit(0);

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("First arg should be private key in Base58 format. Second argument should be address " +
                    "to send to.");
        }
    }
}
