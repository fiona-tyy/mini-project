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
            INSERT INTO users (email, name) values (?,?)
            """;

    public static final String FIND_USER_BY_EMAIL = """
            SELECT * FROM users WHERE email = ?
            """;
    
    // public static final String FIND_USER_BY_ID = """
    //         SELECT * FROM users WHERE id = ?
    //         """;
    
    public static final String ADD_FRIEND = """
            INSERT INTO friends values (?,?)
            """;

    public static final String GET_FRIENDS = """
            SELECT friend_email, users.name as friend_name
            FROM friends JOIN users on friends.friend_email = users.email
            WHERE friends.user_email = ?
            ORDER BY friend_name ASC
            """;

  

    public void addNewUser(String email, String name) throws UserException{
        int result = template.update(INSERT_USER, new Object[]{ email, name});
 
        if (result<1){
         throw new UserException("Error in creating new user");
        }
    }

    public User getUserByEmail(String email) throws UserException{
        SqlRowSet rs = template.queryForRowSet(FIND_USER_BY_EMAIL, email);
        List<User> results = new LinkedList<>();
        while(rs.next()){
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setName(rs.getString("name"));
            user.setPhoneNumber(rs.getString("phone_number"));
            results.add(user);
        }

        if(results.size() <= 0){
            throw new UserException("No user with matching email found.");
        }
        return results.get(0);
    }


    public void addFriend(String firstUserEmail, String secondUserEmail) throws UserException{

        Integer result = template.update(ADD_FRIEND, new Object[]{firstUserEmail, secondUserEmail});
        if(result < 1){
            throw new UserException("Error adding friend");
        }
    }

    public List<Friend> getFriends(String userEmail){
        SqlRowSet rs = template.queryForRowSet(GET_FRIENDS, userEmail);
        List<Friend> friends = new LinkedList<>();
        while(rs.next()){
            Friend friend = new Friend();
            friend.setName(rs.getString("friend_name"));
            friend.setEmail(rs.getString("friend_email"));
            // friend.setPhoneNumber(rs.getString("phone_number"));

            friends.add(friend);
        }
        return friends;
    }
    
}
