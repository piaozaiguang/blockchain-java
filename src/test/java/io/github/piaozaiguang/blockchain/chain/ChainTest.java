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
        chain.genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        // manually sign the genesis transaction
        chain.genesisTransaction.generateSignature(coinbase.privateKey);
        // manually set the transaction id
        chain.genesisTransaction.transactionId = "0";
        // manually add the Transactions Output
        chain.genesisTransaction.outputs.add(new TransactionOutput(chain.genesisTransaction.recipient, chain.genesisTransaction.value, chain.genesisTransaction.transactionId));
        // its important to store our first transaction in the UTXOs list.
        chain.UTXOs.put(chain.genesisTransaction.outputs.get(0).id, chain.genesisTransaction.outputs.get(0));

        System.out.println("Creating and Mining Genesis block... ");
        Block genesis = new Block("0");
        genesis.addTransaction(chain.genesisTransaction);
        chain.addBlock(genesis);

        //testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        chain.addBlock(block1);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        chain.addBlock(block2);
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
        block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
        System.out.println("\nWalletA's balance is: " + walletA.getBalance());
        System.out.println("WalletB's balance is: " + walletB.getBalance());

        chain.isChainValid();
    }

}
