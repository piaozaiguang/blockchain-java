package io.github.piaozaiguang.blockchain.transaction;

import static io.github.piaozaiguang.blockchain.constants.DefaultConstant.MINIMUM_TRANSACTION;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import io.github.piaozaiguang.blockchain.chain.Chain;
import io.github.piaozaiguang.blockchain.utils.HashUtil;
import io.github.piaozaiguang.blockchain.utils.SignatureUtil;
import io.github.piaozaiguang.blockchain.utils.StringUtil;

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
    public String transactionId;
    /**
     * Senders address/public key.
     */
    public PublicKey sender;
    /**
     * Recipients address/public key.
     */
    public PublicKey recipient;
    /**
     * Contains the amount we wish to send to the recipient.
     */
    public float value;
    /**
     * This is to prevent anybody else from spending funds in our wallet.
     */
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

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
    public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction() {

        if(!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        // Gathers transaction inputs (Making sure they are unspent):
        for(TransactionInput i : inputs) {
            i.UTXO = Chain.UTXOs.get(i.transactionOutputId);
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
        outputs.add(new TransactionOutput( this.recipient, value,transactionId));
        // send the left over 'change' back to sender
        outputs.add(new TransactionOutput( this.sender, leftOver,transactionId));

        // Add outputs to Unspent list
        for(TransactionOutput o : outputs) {
            Chain.UTXOs.put(o.id, o);
        }

        // Remove transaction inputs from UTXO lists as spent:
        for(TransactionInput i : inputs) {
            if (i.UTXO == null) {
                // if Transaction can't be found skip it
                continue;
            }
            Chain.UTXOs.remove(i.UTXO.id);
        }

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if (i.UTXO == null) {
                // if Transaction can't be found skip it, This behavior may not be optimal.
                continue;
            }
            total += i.UTXO.value;
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
            total += o.value;
        }
        return total;
    }

    private String calculateHash() {
        // increase the sequence to avoid 2 identical transactions having the same hash
        sequence++;
        return HashUtil.applySha256(StringUtil.base64Encoding(sender) + StringUtil.base64Encoding(recipient) + Float.toString(value) + sequence);
    }
}
