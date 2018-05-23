package io.github.piaozaiguang.blockchain.transaction;

import static io.github.piaozaiguang.blockchain.support.constants.DefaultConstant.MINIMUM_TRANSACTION;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import io.github.piaozaiguang.blockchain.chain.Chain;
import io.github.piaozaiguang.blockchain.support.utils.HashUtil;
import io.github.piaozaiguang.blockchain.support.utils.SignatureUtil;
import io.github.piaozaiguang.blockchain.support.utils.StringUtil;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class Transaction {

    /**
     * Contains a hash of transaction*
     */
    private String transactionId;
    /**
     * Senders address/public key.
     */
    private PublicKey sender;
    /**
     * Recipients address/public key.
     */
    private PublicKey recipient;
    /**
     * Contains the amount we wish to send to the recipient.
     */
    private float value;
    /**
     * This is to prevent anybody else from spending funds in our wallet.
     */
    private byte[] signature;

    private List<TransactionInput> inputs = new ArrayList<>();
    private List<TransactionOutput> outputs = new ArrayList<>();

    /**
     * A rough count of how many transactions have been generated
     */
    private static int sequence = 0;

    /**
     * Constructor
     * @param from
     * @param to
     * @param value
     * @param inputs
     */
    public Transaction(PublicKey from, PublicKey to, float value,  List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getRecipient() {
        return recipient;
    }

    public float getValue() {
        return value;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void addOutput(TransactionOutput output) {
        outputs.add(output);
    }

    public boolean processTransaction() {

        if(!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gathers transaction inputs (Making sure they are unspent):
        for(TransactionInput i : inputs) {
            i.setUTXO(Chain.UTXOs.get(i.getTransactionOutputId()));
        }

        // Checks if transaction is valid:
        if(getInputsValue() < MINIMUM_TRANSACTION) {
            System.out.println("Transaction Inputs to small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs:
        // get value of inputs then the left over change:
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        // send value to recipient
        outputs.add(new TransactionOutput( this.recipient, value, transactionId));
        // send the left over 'change' back to sender
        outputs.add(new TransactionOutput( this.sender, leftOver, transactionId));

        // Add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            Chain.UTXOs.put(o.getId(), o);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if (i.getUTXO() == null) {
                // if Transaction can't be found skip it
                continue;
            }
            Chain.UTXOs.remove(i.getUTXO().getId());
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if (i.getUTXO() == null) {
                // if Transaction can't be found skip it, This behavior may not be optimal.
                continue;
            }
            total += i.getUTXO().getValue();
        }
        return total;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.base64Encoding(sender) + StringUtil.base64Encoding(recipient) + Float.toString(value);
        signature = SignatureUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.base64Encoding(sender) + StringUtil.base64Encoding(recipient) + Float.toString(value);
        return SignatureUtil.verifyECDSASig(sender, data, signature);
    }

    public float getOutputsValue() {
        float total = 0;
        for (TransactionOutput o : outputs) {
            total += o.getValue();
        }
        return total;
    }

    private String calculateHash() {
        // increase the sequence to avoid 2 identical transactions having the same hash
        sequence++;
        return HashUtil.applySha256(StringUtil.base64Encoding(sender) + StringUtil.base64Encoding(recipient) + Float.toString(value) + sequence);
    }
}
