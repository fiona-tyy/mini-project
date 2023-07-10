package tfip.project.appbackend.services;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import tfip.project.appbackend.models.ExpenseProcessed;
import tfip.project.appbackend.models.SettlementData;
import tfip.project.appbackend.models.ShareSplit;

@Service
public class MessageService {

    @Autowired
    private FirebaseMessaging fcm;



    public void subscribeTopic (String registrationToken, String topic) throws FirebaseMessagingException{
        fcm.subscribeToTopic(Arrays.asList(registrationToken), topic);
        System.out.println("registered to topic");
        
    }


    public String sendNotificationToTopic(ExpenseProcessed processed) throws FirebaseMessagingException{
        List<String> topics = new LinkedList<>();
        for(ShareSplit ss : processed.getSharesSplit()){
                topics.add(String.format("'%s' in topics", ss.getEmail().replace("@", "-")));
        }

        String condition = String.join(" || ", topics);
        System.out.println(">>condition: " + condition);

        // String condition = "'abc-email.com' in topics";

        Message msg = Message.builder()
                            .setNotification(Notification.builder().setTitle("New Expense").setBody(processed.getRecordedBy().getName().toUpperCase()+ " added an expense for " + processed.getDescription()).build())
                            .setCondition(condition)
                            .build();
        return fcm.send(msg);
    }
    
    public String sendNotificationToTopic(SettlementData settlement) throws FirebaseMessagingException{

        String condition = String.join(" || ", String.format("'%s' in topics", settlement.getWhoPaid().getEmail().replace("@", "-")),String.format("'%s' in topics", settlement.getWhoReceived().getEmail().replace("@", "-")));

        System.out.println(">>condition: " + condition);

        Message msg = Message.builder()
                            .setNotification(Notification.builder().setTitle("New Payment").setBody(settlement.getWhoPaid().getName().toUpperCase() + " paid " + settlement.getWhoReceived().getName().toUpperCase() + " $" + settlement.getRepaymentAmount()).build())
                            .setCondition(condition)
                            .build();
        return fcm.send(msg);
    }
    
}
