package tfip.project.appbackend.repositories;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import tfip.project.appbackend.models.Friend;
import tfip.project.appbackend.models.User;
import tfip.project.appbackend.services.UserException;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate template;

    public static final String INSERT_USER = """
            INSERT INTO users (id, name, email) values (?,?,?)
            """;

    public static final String FIND_USER_BY_EMAIL = """
            SELECT * FROM users WHERE email = ?
            """;
    
    public static final String FIND_USER_BY_ID = """
            SELECT * FROM users WHERE id = ?
            """;
    
    public static final String ADD_FRIEND = """
            INSERT INTO friends values (?,?)
            """;

    public static final String GET_FRIENDS = """
            SELECT friend_id, users.name as friend_name, users.email as friend_email 
            FROM friends JOIN users on friends.friend_id = users.id
            WHERE friends.user_id = ?
            """;

    public static final String UPDATE_AMOUNT_OWED = """
        UPDATE friends SET amount_outstanding = ? WHERE (user_id = ? AND friend_id = ?)
        """;
            
    public static final String GET_OUTSTANDING_WITH_FRIEND = """
        SELECT amount_outstanding FROM friends
        WHERE (user_id = ? AND friend_id = ?)
        """;

    public void addNewUser(String id, String name, String email) throws UserException{
        int result = template.update(INSERT_USER, new Object[]{ id, name, email});
 
        if (result<1){
         throw new UserException("Error in creating new user");
        }
    }

    public User getUserByEmail(String email) throws UserException{
        SqlRowSet rs = template.queryForRowSet(FIND_USER_BY_EMAIL, email);
        List<User> results = new LinkedList<>();
        while(rs.next()){
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            results.add(user);
        }

        if(results.size() <= 0){
            throw new UserException("No user with matching email found.");
        }
        return results.get(0);
    }

    public User getUserById(String userId) throws UserException{
        SqlRowSet rs = template.queryForRowSet(FIND_USER_BY_ID, userId);
        List<User> results = new LinkedList<>();
        while(rs.next()){
            User user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            results.add(user);
        }

        if(results.size() <= 0){
            throw new UserException("No user with matching id found.");
        }
        return results.get(0);
    }

    public void addFriend(String firstUserId, String secondUserId) throws UserException{

        Integer result = template.update(ADD_FRIEND, new Object[]{firstUserId, secondUserId});
        if(result < 1){
            throw new UserException("Error adding friend");
        }
    }

    public List<Friend> getFriends(String userId){
        SqlRowSet rs = template.queryForRowSet(GET_FRIENDS, userId);
        List<Friend> friends = new LinkedList<>();
        while(rs.next()){
            Friend friend = new Friend();
            friend.setId(rs.getString("friend_id"));
            friend.setName(rs.getString("friend_name"));
            friend.setEmail(rs.getString("friend_email"));

            friends.add(friend);
        }
        return friends;
    }
    
    // public void updateOutstanding(BigDecimal amount, String userId, String friendId) throws UserException{
        
    //     BigDecimal currentOutstanding = getOutstandingWithFriend(userId, friendId);
    //     BigDecimal newOutstanding = currentOutstanding.add(amount);
    //     template.update(UPDATE_AMOUNT_OWED, new Object[]{newOutstanding, userId, friendId});
    // }

    // public BigDecimal getOutstandingWithFriend(String userId, String friendId) throws UserException{
    //     try {
    //         BigDecimal result = template.queryForObject(GET_OUTSTANDING_WITH_FRIEND,  BigDecimal.class, new Object[]{userId, friendId});
    //         return result;
    //     } catch (DataAccessException ex) {
    //         addFriend(userId, friendId);
    //         addFriend(friendId, userId);
    //         return BigDecimal.ZERO;
    //     }
       
    // }
}
