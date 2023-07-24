package tfip.project.appbackend.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import tfip.project.appbackend.models.ExpenseData;
import tfip.project.appbackend.models.ExpenseProcessed;
import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.models.LineItem;
import tfip.project.appbackend.models.SettlementData;
import tfip.project.appbackend.models.ShareSplit;
import tfip.project.appbackend.models.Transaction;
import tfip.project.appbackend.models.User;
import tfip.project.appbackend.repositories.ReceiptRepository;
import tfip.project.appbackend.repositories.TransactionSQLRepository;
import tfip.project.appbackend.repositories.UserRepository;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private TransactionSQLRepository transSQLRepo;

    @Autowired
    private ReceiptRepository receiptRepo;

    @Transactional(rollbackFor = TransactionException.class)
    public ExpenseProcessed processTransaction (ExpenseData trans, MultipartFile file) throws UserException, TransactionException{

        ExpenseProcessed processedTrans = new ExpenseProcessed();
        String transactionId  = UUID.randomUUID().toString().substring(0, 8);
        processedTrans.setTransactionId(transactionId);
        processedTrans.setDescription(trans.getDescription());
        processedTrans.setDate(trans.getDate());
        processedTrans.setWhoPaid(trans.getRecordedBy());
        processedTrans.setRecordedBy(trans.getRecordedBy());
        processedTrans.setRecordedDate(trans.getRecordedDate());
        processedTrans.setTransactionType(trans.getTransactionType());
        processedTrans.setAttachment(trans.getAttachment());
    
        //logic to calculate split with
        List<String> sharing = new LinkedList<>();
        List<ShareSplit> sharesSplit = new LinkedList<>();
        
        BigDecimal totalAmount =  BigDecimal.ZERO;
        for(LineItem it: trans.getLineItems()){
            
            totalAmount = totalAmount.add(it.getAmount());
            BigDecimal average = it.getAmount().divide(BigDecimal.valueOf(it.getSplitWith().size()), 4, RoundingMode.HALF_EVEN);

            for (String str : it.getSplitWith()){
                if(!sharing.contains(str)){
                    sharing.add(str);
                    ShareSplit share = new ShareSplit();
                    share.setEmail(str);
                    share.setShareAmount(average);
                    User user;
                    user = userRepo.getUserByEmail(str);
                    share.setName(user.getName());
                    sharesSplit.add(share);
                   
                } else {
                    for(ShareSplit s : sharesSplit){
                        if(s.getEmail().equals(str)){
                            s.setShareAmount(s.getShareAmount().add(average));
                        }
                    }
                }
                
            }
          
        }
        
        for (ShareSplit s : sharesSplit){

            BigDecimal taxSplit = (s.getShareAmount().divide(totalAmount,4, RoundingMode.HALF_EVEN).multiply(trans.getGst().add(trans.getServiceCharge())));
            s.setShareAmount((s.getShareAmount().add(taxSplit)).setScale(2, RoundingMode.HALF_EVEN));
            }

        processedTrans.setSharesSplit(sharesSplit);
        totalAmount = totalAmount.add(trans.getGst());
        totalAmount = totalAmount.add(trans.getServiceCharge());
        processedTrans.setTotalAmount(totalAmount);
       
        transSQLRepo.addTransaction(processedTrans.getTransactionId(), processedTrans.getTransactionType(), processedTrans.getDescription(), processedTrans.getDate(), processedTrans.getTotalAmount(), processedTrans.getRecordedBy().getEmail(), processedTrans.getRecordedDate(), processedTrans.getAttachment());

        for (ShareSplit s : sharesSplit){

            if(!s.getEmail().equals( trans.getWhoPaid().getEmail())){
                transSQLRepo.addLoanOrPayment(transactionId, trans.getWhoPaid().getEmail(), s.getEmail(), s.getShareAmount());
                }
            }
        if(file != null){
            try {
                receiptRepo.uploadReceipt(transactionId, file);
            } catch (IOException e) {
                throw new TransactionException("Error uploading file");
            }
        }
        return processedTrans;
    }

    @Transactional(rollbackFor = TransactionException.class)
    public SettlementData recordSettlement(SettlementData payment, MultipartFile file) throws TransactionException{

        
        String transactionId  = UUID.randomUUID().toString().substring(0, 8);
        payment.setTransactionId(transactionId);
        transSQLRepo.addTransaction(transactionId, payment.getTransactionType(), payment.getDescription(), payment.getDate(), payment.getRepaymentAmount(), payment.getRecordedBy().getEmail(), payment.getRecordedDate(), payment.getAttachment());
        transSQLRepo.addLoanOrPayment(transactionId, payment.getWhoPaid().getEmail(), payment.getWhoReceived().getEmail(), payment.getRepaymentAmount());
        if(file != null){
            try {
                receiptRepo.uploadReceipt(transactionId, file);
            } catch (IOException e) {
                throw new TransactionException("Error uploading file");
            }
        }

        return payment;
    }

    public List<Friend> getOutstandingWithFriends(String userEmail){

        return transSQLRepo.getOutstandingWithFriends(userEmail);
    }

    public List<Transaction> getTransactionsWithFriend(String userEmail, String friendEmail){
        return transSQLRepo.getTransactionsWithFriend(userEmail, friendEmail);
    }

    public List<Transaction> getTransactionDetailById(String transactionId){
        return transSQLRepo.getTransactionDetailById(transactionId);
    }

   public void deleteTransaction(String transactionId) throws TransactionException{
        transSQLRepo.deleteTransaction(transactionId);
   }

   public List<Transaction> getRecentTransactions(String userEmail){
        return transSQLRepo.getRecentTransactions(userEmail);
   }

}
