package io.github.piaozaiguang.blockchain.transaction;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class TransactionInput {
    /**
     * Reference to TransactionOutputs -> transactionId
     */
    public String transactionOutputId;
    /**
     * Contains the Unspent transaction output
     */
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }
}
