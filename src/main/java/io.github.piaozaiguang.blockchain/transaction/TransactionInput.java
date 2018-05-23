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
    private String transactionOutputId;
    /**
     * Contains the Unspent transaction output
     */
    private TransactionOutput UTXO;

    /**
     * Constructor
     * @param transactionOutputId
     */
    public TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId;
    }

    public String getTransactionOutputId() {
        return transactionOutputId;
    }

    public TransactionOutput getUTXO() {
        return UTXO;
    }

    public void setUTXO(TransactionOutput UTXO) {
        this.UTXO = UTXO;
    }
}
