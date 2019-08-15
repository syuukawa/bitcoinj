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
public class GetBalanceByPrivate {
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
            String privateKey = "cRmuAWFo9iz3CVr4W64ixFG9XFFiwc7TCbwHmUkVdUEykeM38RLP";
            if (privateKey.length() == 51 || privateKey.length() == 52) {
                DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(params, privateKey);
                key = dumpedPrivateKey.getKey();
            } else {
                BigInteger privKey = Base58.decodeToBigInteger(privateKey);
                key = ECKey.fromPrivate(privKey);
            }

            System.out.println("Address from private key is: " + LegacyAddress.fromKey(params, key).toString());

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
            System.out.println("Wallet " + wallet.toString());
            System.out.println("Balance " + wallet.getBalance().toFriendlyString());

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("First arg should be private key in Base58 format. Second argument should be address " +
                    "to send to.");
        }
    }
}
