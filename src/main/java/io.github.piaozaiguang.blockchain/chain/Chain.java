package io.github.piaozaiguang.blockchain.chain;

import static io.github.piaozaiguang.blockchain.support.constants.DefaultConstant.DIFFICULTY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.piaozaiguang.blockchain.block.Block;
import io.github.piaozaiguang.blockchain.support.utils.StringUtil;
import io.github.piaozaiguang.blockchain.transaction.Transaction;
import io.github.piaozaiguang.blockchain.transaction.TransactionInput;
import io.github.piaozaiguang.blockchain.transaction.TransactionOutput;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class Chain {
    private List<Block> blockchain = new ArrayList<>();
    public static Map<String, TransactionOutput> UTXOs = new HashMap<>();

    private Transaction genesisTransaction;

    public void putUTXO(String id, TransactionOutput output) {
        UTXOs.put(id, output);
    }

    public void setGenesisTransaction(Transaction genesisTransaction) {
        this.genesisTransaction = genesisTransaction;
    }

    public boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = StringUtil.getDifficultyString(DIFFICULTY);
        // a temporary working list of unspent transactions at a given block state.
        Map<String,TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(genesisTransaction.getOutputs().get(0).getId(), genesisTransaction.getOutputs().get(0));

        // loop through blockchain to check hashes:
        for(int i = 1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            // compare registered hash and calculated hash:
            if(!currentBlock.getHash().equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }
            // compare previous hash and registered previous hash
            if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }
            // check if hash is solved
            if(!currentBlock.getHash().substring(0, DIFFICULTY).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }

            // loop thru blockchains transactions:
            TransactionOutput tempOutput;
            for(int t=0; t <currentBlock.getTransactions().size(); t++) {
                Transaction currentTransaction = currentBlock.getTransactions().get(t);

                if(!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for(TransactionInput input: currentTransaction.getInputs()) {
                    tempOutput = tempUTXOs.get(input.getTransactionOutputId());

                    if(tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
                        return false;
                    }

                    if(input.getUTXO().getValue() != tempOutput.getValue()) {
                        System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.getTransactionOutputId());
                }

                for(TransactionOutput output: currentTransaction.getOutputs()) {
                    tempUTXOs.put(output.getId(), output);
                }

                if( currentTransaction.getOutputs().get(0).getRecipient() != currentTransaction.getRecipient()) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }
                if( currentTransaction.getOutputs().get(1).getRecipient() != currentTransaction.getSender()) {
                    System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
                    return false;
                }

            }

        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public void addBlock(Block newBlock) {
        newBlock.mineBlock(DIFFICULTY);
        blockchain.add(newBlock);
    }
}
