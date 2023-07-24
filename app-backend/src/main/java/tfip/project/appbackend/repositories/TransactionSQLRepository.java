package tfip.project.appbackend.repositories;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.models.ShareSplit;
import tfip.project.appbackend.models.Transaction;
import tfip.project.appbackend.models.User;
import tfip.project.appbackend.services.TransactionException;

@Repository
public class TransactionSQLRepository {

    @Autowired
    private JdbcTemplate template;

    public static final String ADD_TRANSACTION = """
            INSERT INTO transactions (id, transaction_type, description, date, total_amount, recorded_by, recorded_date, attachment)
            VALUES (?,?,?,?,?,?,?,?)
            """;
    
    public static final String ADD_LOAN_OR_PAYMENT = """
            INSERT INTO loans (transaction_id, lender_email, borrower_email, amount) VALUES (?,?,?,?)
            """;
    
    public static final String GET_AMOUNT_OUTSTANDING = """
            SELECT t3.email as email, users.name as name, lent, borrowed, amount_outstanding from 
            (SELECT IFNULL(t1.borrower_email ,t2.lender_email) as email, IFNULL(amount_lent,0) as lent, IFNULL(amount_borrowed,0) as borrowed,IFNULL(amount_lent,0)-IFNULL(amount_borrowed,0) as amount_outstanding
            FROM
            (SELECT borrower_email, SUM(amount) as amount_lent FROM loans WHERE lender_email = ? group by borrower_email) t1
            LEFT JOIN
            (SELECT lender_email, SUM(amount) as amount_borrowed FROM loans WHERE borrower_email = ? group by lender_email) t2
            ON (t1.borrower_email = t2.lender_email)
            UNION
            SELECT IFNULL(t1.borrower_email ,t2.lender_email) as email, IFNULL(amount_lent,0) as lent, IFNULL(amount_borrowed,0) as borrowed, IFNULL(amount_lent,0)-IFNULL(amount_borrowed,0) as amount_outstanding
            FROM
            (SELECT borrower_email, SUM(amount) as amount_lent FROM loans WHERE lender_email = ? group by borrower_email) t1
            RIGHT JOIN
            (SELECT lender_email, SUM(amount) as amount_borrowed FROM loans WHERE borrower_email = ? group by lender_email) t2
            ON (t1.borrower_email = t2.lender_email)) t3
            LEFT JOIN users on t3.email = users.email;
            """;
  
    public final String GET_TRANSACTIONS_WITH_FRIEND = """
            SELECT transaction_id, transaction_type, description, date, total_amount, lender_email, T1.name as lender_name, borrower_email, T2.name as borrower_name, amount 
            FROM loans JOIN transactions 
            ON loans.transaction_id=transactions.id
            JOIN users AS T1 ON loans.lender_email=T1.email
            JOIN users as T2 ON loans.borrower_email=T2.email
            WHERE ((lender_email = ? and borrower_email = ?) OR (lender_email = ? and borrower_email = ?))
            ORDER BY date DESC
            LIMIT 20
            """;
   
    public final String GET_TRANSACTION_DETAIL_BY_ID = """
            SELECT transactions.*,T3.name as recorder_name, lender_email, T1.name as lender_name, borrower_email, T2.name as borrower_name, amount 
            FROM loans JOIN transactions 
            ON loans.transaction_id=transactions.id 
            JOIN users AS T1 ON loans.lender_email=T1.email
            JOIN users AS T2 ON loans.borrower_email=T2.email
            JOIN users AS T3 ON transactions.recorded_by=T3.email
            WHERE transaction_id = ?
            """;

    public final String DELETE_TRANSACTION = """
            DELETE FROM transactions WHERE id = ?
            """;
    public final String GET_RECENT_TRANSACTIONS = """
            SELECT transaction_id, transaction_type, description, recorded_by, T3.name as recorder_name, recorded_date,lender_email, T1.name as lender_name, borrower_email, T2.name as borrower_name, amount 
            FROM loans JOIN transactions 
            ON loans.transaction_id=transactions.id 
            JOIN users AS T1 ON loans.lender_email=T1.email
            JOIN users AS T2 ON loans.borrower_email=T2.email
            JOIN users AS T3 ON transactions.recorded_by=T3.email
            WHERE (lender_email = ? or borrower_email = ?) 
            ORDER BY recorded_date DESC LIMIT 5
            """;

    public void addTransaction(String transactionId, String transactionType, String desc, long date, BigDecimal totalAmount, String recordedBy, long recordedDate, String attachment) throws TransactionException{

        int result = template.update(ADD_TRANSACTION, new Object[]{transactionId, transactionType, desc, date, totalAmount,recordedBy,recordedDate, attachment});

        if(result <1){
            throw new TransactionException("Error saving transaction");
        }
    }

    public void addLoanOrPayment(String transactionId, String lenderEmail, String borrowerEmail, BigDecimal borrowedAmount) throws TransactionException{

        int result = template.update(ADD_LOAN_OR_PAYMENT, new Object[]{transactionId, lenderEmail, borrowerEmail, borrowedAmount});

    if(result < 1){
        throw new TransactionException("Error saving loan/payment");
    }
    }

    public List<Friend> getOutstandingWithFriends(String userEmail){
        List<Friend> friends = new LinkedList<>();
        SqlRowSet rs = template.queryForRowSet(GET_AMOUNT_OUTSTANDING, userEmail, userEmail, userEmail, userEmail);
        while(rs.next()){
            Friend friend = new Friend();
            friend.setEmail(rs.getString("email"));
            friend.setName(rs.getString("name"));
            friend.setAmountOutstanding(rs.getBigDecimal("amount_outstanding"));
            friends.add(friend);
        }

        return friends;
    }

    public List<Transaction> getTransactionsWithFriend(String userId, String friendId){
        List<Transaction> transactions = new LinkedList<>();
        SqlRowSet rs = template.queryForRowSet(GET_TRANSACTIONS_WITH_FRIEND, userId, friendId, friendId, userId);
        while(rs.next()){
            Transaction trans = new Transaction();
            trans.setTransactionId(rs.getString( "transaction_id"));
            trans.setTransactionType(rs.getString("transaction_type"));
            trans.setDescription(rs.getString("description"));
            trans.setDate(rs.getLong("date"));
            trans.setTotalAmount(rs.getBigDecimal("total_amount"));
            User whoPaid = new User();
            whoPaid.setEmail(rs.getString("lender_email"));
            whoPaid.setName(rs.getString("lender_name"));
            trans.setWhoPaid(whoPaid);
            ShareSplit borrower = new ShareSplit();
            borrower.setEmail(rs.getString("borrower_email"));
            borrower.setName(rs.getString("borrower_name"));
            borrower.setShareAmount(rs.getBigDecimal("amount"));
            trans.setWhoBorrowed(borrower);

            transactions.add(trans);
        }
        return transactions;
    }

    public List<Transaction> getTransactionDetailById(String transactionId){
        List<Transaction> transactions = new LinkedList<>();
        SqlRowSet rs = template.queryForRowSet(GET_TRANSACTION_DETAIL_BY_ID, transactionId);
        while(rs.next()){
                Transaction trans = new Transaction();
                trans.setTransactionId(rs.getString( "id"));
                trans.setTransactionType(rs.getString("transaction_type"));
                trans.setDescription(rs.getString("description"));
                trans.setDate(rs.getLong("date"));
                trans.setTotalAmount(rs.getBigDecimal("total_amount"));
                trans.setRecordedDate(rs.getLong("recorded_date"));
                trans.setAttachment(rs.getString("attachment"));
                User recorder = new User();
                recorder.setEmail(rs.getString("recorded_by"));
                recorder.setName(rs.getString("recorder_name"));
                trans.setRecordedBy(recorder);
                User whoPaid = new User();
                whoPaid.setEmail(rs.getString("lender_email"));
                whoPaid.setName(rs.getString("lender_name"));
                trans.setWhoPaid(whoPaid);
                ShareSplit share = new ShareSplit();
                share.setEmail(rs.getString("borrower_email"));
                share.setName(rs.getString("borrower_name"));
                share.setShareAmount(rs.getBigDecimal("amount"));
                trans.setWhoBorrowed(share);

                transactions.add(trans); 
        }
        return transactions;
    }

    public void deleteTransaction(String transactionId) throws TransactionException{
        int result = template.update(DELETE_TRANSACTION, transactionId);
        if(result <1){
            throw new TransactionException("Failed to delete transaction.");
        }
    }

    public List<Transaction> getRecentTransactions(String userEmail){

        List<Transaction> transactions = new LinkedList<>();
        SqlRowSet rs = template.queryForRowSet(GET_RECENT_TRANSACTIONS, userEmail, userEmail);
        while(rs.next()){
            Transaction trans = new Transaction();
            trans.setTransactionId(rs.getString( "transaction_id"));
            trans.setTransactionType(rs.getString("transaction_type"));
            trans.setDescription(rs.getString("description"));
            trans.setRecordedDate(rs.getLong("recorded_date"));
            User recorder = new User();
            recorder.setEmail(rs.getString("recorded_by"));
            recorder.setName(rs.getString("recorder_name"));
            trans.setRecordedBy(recorder);
            User whoPaid = new User();
            whoPaid.setEmail(rs.getString("lender_email"));
            whoPaid.setName(rs.getString("lender_name"));
            trans.setWhoPaid(whoPaid);
            ShareSplit share = new ShareSplit();
            share.setEmail(rs.getString("borrower_email"));
            share.setName(rs.getString("borrower_name"));
            share.setShareAmount(rs.getBigDecimal("amount"));
            trans.setWhoBorrowed(share);
            transactions.add(trans); 
        }
        return transactions;
    }


}
