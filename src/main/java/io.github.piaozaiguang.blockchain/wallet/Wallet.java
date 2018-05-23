package io.github.piaozaiguang.blockchain.wallet;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.piaozaiguang.blockchain.chain.Chain;
import io.github.piaozaiguang.blockchain.support.utils.KeyPairUtil;
import io.github.piaozaiguang.blockchain.transaction.Transaction;
import io.github.piaozaiguang.blockchain.transaction.TransactionInput;
import io.github.piaozaiguang.blockchain.transaction.TransactionOutput;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class Wallet {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Map<String,TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        KeyPair keyPair = KeyPairUtil.generate();
        // Set the public and private keys from the keyPair
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: Chain.UTXOs.entrySet()) {
            TransactionOutput UTXO = item.getValue();
            //if output belongs to me ( if coins belong to me )
            if(UTXO.isMine(publicKey)) {
                //add it to our list of unspent transactions.
                UTXOs.put(UTXO.getId(),UTXO);
                total += UTXO.getValue();
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, float value) {
        if(getBalance() < value) {
            System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
            return null;
        }
        List<TransactionInput> inputs = new ArrayList<>();

        float total = 0;
        for (Map.Entry<String, TransactionOutput> item: UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.getValue();
            inputs.add(new TransactionInput(UTXO.getId()));
            if (total > value) {
                break;
            }
        }

        Transaction newTransaction = new Transaction(publicKey, recipient , value, inputs);
        newTransaction.generateSignature(privateKey);

        for(TransactionInput input: inputs){
            UTXOs.remove(input.getTransactionOutputId());
        }

        return newTransaction;
    }
}
