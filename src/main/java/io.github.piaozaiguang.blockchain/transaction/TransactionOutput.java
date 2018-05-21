package io.github.piaozaiguang.blockchain.transaction;

import java.security.PublicKey;

import io.github.piaozaiguang.blockchain.utils.HashUtil;
import io.github.piaozaiguang.blockchain.utils.StringUtil;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class TransactionOutput {

    public String id;
    /**
     * also known as the new owner of these coins.
     */
    public PublicKey recipient;
    /**
     * the amount of coins they own
     */
    public float value;
    /**
     * the id of the transaction this output was created in
     */
    public String parentTransactionId;

    /**
     * Constructor
     * @param recipient
     * @param value
     * @param parentTransactionId
     */
    public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient;
        this.value = value;
        this.parentTransactionId = parentTransactionId;
        this.id = HashUtil.applySha256(StringUtil.base64Encoding(recipient) + Float.toString(value) + parentTransactionId);
    }

    /**
     * Check if coin belongs to you
     * @param publicKey
     * @return
     */
    public boolean isMine(PublicKey publicKey) {
        return (publicKey == recipient);
    }
}
