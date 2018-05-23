package io.github.piaozaiguang.blockchain.chain;

import java.security.Security;

import org.junit.Test;

import io.github.piaozaiguang.blockchain.block.Block;
import io.github.piaozaiguang.blockchain.transaction.Transaction;
import io.github.piaozaiguang.blockchain.transaction.TransactionOutput;
import io.github.piaozaiguang.blockchain.wallet.Wallet;

public class ChainTest {

    @Test
    public void test() {
        Chain chain = new Chain();

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // Create wallets:
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet coinbase = new Wallet();

        // create genesis transaction, which sends 100 Coin to walletA:
        Transaction genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f, null);
        // manually sign the genesis transaction
        genesisTransaction.generateSignature(coinbase.getPrivateKey());
        // manually set the transaction id
        genesisTransaction.setTransactionId("0");
        // manually add the Transactions Output
        TransactionOutput transactionOutput = new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(),
                              genesisTransaction.getTransactionId());
        genesisTransaction.addOutput(transactionOutput);
        chain.setGenesisTransaction(genesisTransaction);

        // its important to store our first transaction in the UTXOs list.
        chain.putUTXO(transactionOutput.getId(), transactionOutput);

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        chain.addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.getHash());
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
        chain.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.getHash());
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
        chain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.getHash());
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        chain.isChainValid();
    }

}
