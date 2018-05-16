package io.github.piaozaiguang.blockchain;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class TransactionInput {
    public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
    public TransactionOutput UTXO; //Contains the Unspent transaction output

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
