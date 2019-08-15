package org.bitcoinj.examples.test;


import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.math.BigInteger;
import java.util.List;

public class HdWalletBTC {

    private static String passwordBtc = "12345679";

    private static String wordsList = "visa immune silly edit typical first demand baby evoke cabbage false cousin kitten poem mass";
    private static final byte[] SEED = null;

    private static NetworkParameters params = RegTestParams.get();

    public static void main(String args[]) {

        try {
            DeterministicSeed deterministicSeed = new DeterministicSeed(wordsList, SEED, passwordBtc, 0L);
            System.out.println("BIP39 seed: " + deterministicSeed.toHexString());

            /**生成根私钥 root private key*/
            DeterministicKey rootPrivateKey = HDKeyDerivation.createMasterPrivateKey(deterministicSeed.getSeedBytes());
            System.out.println("BIP32 rootPrivateKey" + rootPrivateKey);
//            System.out.println("publicKey" + rootPrivateKey.serializePublic(params));

            /**根私钥进行 priB58编码*/
            String priv = rootPrivateKey.serializePrivB58(params);
            System.out.println("BIP32 Root Key: " + priv);

            /**由根私钥生成HD钱包*/
            DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(rootPrivateKey);
            /**定义父路径*/
            List<ChildNumber> parsePath = HDUtils.parsePath("M/44H/0H/0H/0/");

            // 循环生成100个账号
            DeterministicKey accountKey0 = deterministicHierarchy.get(parsePath, true, true);
            System.out.println("BIP32 Extended Private Key: " + accountKey0.serializePrivB58(params)); //TODO OK
            System.out.println("BIP32 Extended Public Key: " + accountKey0.serializePubB58(params)); //TODO OK

            //TODO 循环生成10个账号；10的数量可以从配置文件获取
            for (int index = 10; index < 13; index++) {
                /**由父路径,派生出子私钥*/

                DeterministicKey childKey0 = deterministicHierarchy.deriveChild(parsePath, true, true, new ChildNumber(index));
    //            System.out.println("BIP32 extended 0 private key:{}" + childKey0.serializePrivB58(mainnetParams));
    //            System.out.println("BIP32 extended 0 public key:{}" + childKey0.serializePubB58(mainnetParams));

                BigInteger privKey = childKey0.getPrivKey();
                ECKey ecKey = ECKey.fromPrivate(privKey);
//                Address address = ecKey.toAddress(params);

                System.out.println("private Key:  " + childKey0.getPrivateKeyAsWiF(params)); //TODO OK
                Address address = Address.fromKey(params,ecKey, Script.ScriptType.P2PKH);
//                System.out.println("address:  " + childKey0.toAddress(params)); //TODO OK
                System.out.println("address:  " + address.toString());
                System.out.println("public Key:  " + childKey0.getPublicKeyAsHex()); //TODO OK
            }
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
    }
}
