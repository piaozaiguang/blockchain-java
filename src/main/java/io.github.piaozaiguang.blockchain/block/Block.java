package io.github.piaozaiguang.blockchain.block;

import java.util.ArrayList;

import io.github.piaozaiguang.blockchain.transaction.Transaction;
import io.github.piaozaiguang.blockchain.utils.HashUtil;
import io.github.piaozaiguang.blockchain.utils.MerkleRootUtil;
import io.github.piaozaiguang.blockchain.utils.StringUtil;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class Block {
    public String hash;
    public String previousHash;
    public String merkleRoot;
    /**
     * our data will be a simple message.
     */
    public ArrayList<Transaction> transactions = new ArrayList<>();
    /**
     * as number of milliseconds since 1/1/1970.
     */
    public long timeStamp;
    public int nonce;

    /**
     * Block Constructor.
     * @param previousHash
     */
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timeStamp = System.currentTimeMillis();
        // Making sure we do this after we set the other values.
        this.hash = calculateHash();
    }

    /**
     * Calculate new hash based on blocks contents
     * @return
     */
    public String calculateHash() {
        return HashUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
    }

    /**
     * Increases nonce value until hash target is reached.
     * @param difficulty
     */
    public void mineBlock(int difficulty) {
        merkleRoot = MerkleRootUtil.getMerkleRoot(transactions);
        // Create a string with difficulty * "0"
        String target = StringUtil.getDifficultyString(difficulty);
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    /**
     * Add transactions to this block
     * @param transaction
     * @return
     */
    public boolean addTransaction(Transaction transaction) {
        // process transaction and check if valid, unless block is genesis block then ignore.
        if (transaction == null) {
            return false;
        }
        if (!"0".equals(previousHash)) {
            if (!transaction.processTransaction()) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }
}
