package tfip.project.appbackend.repositories;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


//can delete
@Repository
public class AccountBalanceRepository {
    
    @Autowired
    private JdbcTemplate template;

    public static final String UPDATE_AMOUNT_OWED = """
        UPDATE friends SET amount_outstanding = ? WHERE (user_id = ? AND friend_id = ?)
        """;
    
    public static final String GET_OUTSTANDING_WITH_FRIEND = """
        SELECT amount_outstanding FROM friends
        WHERE (user_id = ? AND friend_id = ?)
        """;

    //update amount outstanding
    // public void updateOutstanding(BigDecimal amount, String userId, String friendId){
        
    //     BigDecimal currentOutstanding = getOutstandingWithFriend(userId, friendId);
    //     BigDecimal newOutstanding = currentOutstanding.add(amount);
    //     template.update(UPDATE_AMOUNT_OWED, new Object[]{newOutstanding, userId, friendId});
    // }

    // public BigDecimal getOutstandingWithFriend(String userId, String friendId){
    //     BigDecimal result = template.queryForObject(GET_OUTSTANDING_WITH_FRIEND,  BigDecimal.class, new Object[]{userId, friendId});
    //     if (result == null)
    //     return result;
    // }
}
