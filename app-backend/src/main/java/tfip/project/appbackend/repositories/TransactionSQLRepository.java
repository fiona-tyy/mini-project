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
            INSERT INTO loans (transaction_id, lender_id, borrower_id, amount) VALUES (?,?,?,?)
            """;
    
    public static final String GET_AMOUNT_OUTSTANDING = """
            SELECT t3.id as id, users.name as name, users.email as email, lent, borrowed, amount_outstanding from 
            (SELECT IFNULL(t1.borrower_id ,t2.lender_id) as id, IFNULL(amount_lent,0) as lent, IFNULL(amount_borrowed,0) as borrowed,IFNULL(amount_lent,0)-IFNULL(amount_borrowed,0) as amount_outstanding
            FROM
            (SELECT borrower_id, SUM(amount) as amount_lent FROM loans WHERE lender_Id = ? group by borrower_id) t1
            LEFT JOIN
            (SELECT lender_id, SUM(amount) as amount_borrowed FROM loans WHERE borrower_Id = ? group by lender_id) t2
            ON (t1.borrower_id = t2.lender_id)
            UNION
            SELECT IFNULL(t1.borrower_id ,t2.lender_id) as id, IFNULL(amount_lent,0) as lent, IFNULL(amount_borrowed,0) as borrowed, IFNULL(amount_lent,0)-IFNULL(amount_borrowed,0) as amount_outstanding
            FROM
            (SELECT borrower_id, SUM(amount) as amount_lent FROM loans WHERE lender_Id = ? group by borrower_id) t1
            RIGHT JOIN
            (SELECT lender_id, SUM(amount) as amount_borrowed FROM loans WHERE borrower_Id = ? group by lender_id) t2
            ON (t1.borrower_id = t2.lender_id)) t3
            LEFT JOIN users on t3.id = users.id;
            """;
  
    public final String GET_TRANSACTIONS_WITH_FRIEND = """
            SELECT transaction_id, transaction_type, description, date, total_amount, lender_id, T1.name as lender_name, borrower_id, T2.name as borrower_name, amount 
            FROM loans JOIN transactions 
            ON loans.transaction_id=transactions.id
            JOIN users AS T1 ON loans.lender_id=T1.id
            JOIN users as T2 ON loans.borrower_id=T2.id
            WHERE ((lender_id = ? and borrower_id = ?) OR (lender_id = ? and borrower_id = ?))
            ORDER BY date DESC
            LIMIT 20
            """;
   
    public final String GET_TRANSACTION_DETAIL_BY_ID = """
            SELECT transactions.*,T3.name as recorder_name, lender_id, T1.name as lender_name, borrower_id, T2.name as borrower_name, amount 
            FROM loans JOIN transactions 
            ON loans.transaction_id=transactions.id 
            JOIN users AS T1 ON loans.lender_id=T1.id
            JOIN users AS T2 ON loans.borrower_id=T2.id
            JOIN users AS T3 ON transactions.recorded_by=t3.id
            WHERE transaction_id = ?
            """;

    public final String DELETE_TRANSACTION = """
            DELETE FROM transactions WHERE id = ?
            """;

    public void addTransaction(String transactionId, String transactionType, String desc, long date, BigDecimal totalAmount, String recordedBy, long recordedDate, String attachment) throws TransactionException{

        int result = template.update(ADD_TRANSACTION, new Object[]{transactionId, transactionType, desc, date, totalAmount,recordedBy,recordedDate, attachment});

        if(result <1){
            throw new TransactionException("Error saving transaction");
        }
    }

    public void addLoanOrPayment(String transactionId, String lenderId, String borrowerId, BigDecimal borrowedAmount) throws TransactionException{

        int result = template.update(ADD_LOAN_OR_PAYMENT, new Object[]{transactionId, lenderId, borrowerId, borrowedAmount});

    if(result < 1){
        throw new TransactionException("Error saving loan/payment");
    }
    }

    public List<Friend> getOutstandingWithFriends(String userId){
        List<Friend> friends = new LinkedList<>();
        SqlRowSet rs = template.queryForRowSet(GET_AMOUNT_OUTSTANDING, userId, userId, userId, userId);
        while(rs.next()){
            Friend friend = new Friend();
            friend.setId(rs.getString("id"));
            friend.setName(rs.getString("name"));
            friend.setEmail(rs.getString("email"));
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
            whoPaid.setId(rs.getString("lender_id"));
            whoPaid.setName(rs.getString("lender_name"));
            trans.setWhoPaid(whoPaid);
            ShareSplit borrower = new ShareSplit();
            borrower.setId(rs.getString("borrower_id"));
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
                recorder.setId(rs.getString("recorded_by"));
                recorder.setName(rs.getString("recorder_name"));
                trans.setRecordedBy(recorder);
                User whoPaid = new User();
                whoPaid.setId(rs.getString("lender_id"));
                whoPaid.setName(rs.getString("lender_name"));
                trans.setWhoPaid(whoPaid);
                ShareSplit share = new ShareSplit();
                share.setId(rs.getString("borrower_id"));
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


}
