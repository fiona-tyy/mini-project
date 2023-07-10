package tfip.project.appbackend.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import tfip.project.appbackend.models.ExpenseProcessed;

//to delete
@Repository
public class TransactionRepository {
    
    @Autowired
    private MongoTemplate template;

    private static final String COLLECTION_NAME = "records";

    public void logTransaction(ExpenseProcessed trans) throws JsonProcessingException{

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(trans);
        Document doc = Document.parse(jsonStr);
        template.insert(doc, COLLECTION_NAME);
    }

    public String getTransactionById(String transactionId){

        Criteria criteria = Criteria.where("transaction_id").is(transactionId);
        Query query = Query.query(criteria);
        query.fields().exclude("_id");
        String transStr = template.findOne(query, String.class, COLLECTION_NAME);
        // System.out.println("retrieved from mongo: " + transStr);
        return transStr;
    }

    // public List<Transaction> getTransactionsWithFriend(String userId, String friendId){
    // // public List<String> getTransactionsWithFriend(String userId, String friendId){

    //     Criteria criteria1 = new Criteria();
    //     criteria1.andOperator(Criteria.where("who_paid.id").is(userId), Criteria.where("shares_split.id").is(friendId));
    //     Criteria criteria2 = new Criteria();
    //     criteria2.andOperator(Criteria.where("who_paid.id").is(friendId), Criteria.where("shares_split.id").is(userId));

    //     Criteria criteria = new Criteria();
    //     criteria.orOperator(criteria1, criteria2);

    //     AggregationOperation unwindSharesSplit = Aggregation.unwind("shares_split");
    //     AggregationOperation match = Aggregation.match(criteria);
    //     ProjectionOperation project = Aggregation.project("transaction_id","description","date", "who_paid", "recorded_by", "recorded_date", "total_amount", "shares_split");
    //     AggregationOperation sortByDate = Aggregation.sort(Direction.DESC, "date");
    //     Aggregation pipeline = Aggregation.newAggregation(unwindSharesSplit, match,project, sortByDate);

    //     AggregationResults<Document> results = template.aggregate(pipeline, COLLECTION_NAME, Document.class);
    //     List<Transaction> transactions = results.getMappedResults().stream()
    //                                                 .map( doc -> {
    //                                                     Transaction t = new Transaction();
    //                                                     t.setTransactionId(doc.getString("transaction_id"));
    //                                                     t.setDescription(doc.getString("description"));
    //                                                     if(doc.get("total_amount").getClass() == Double.class){
    //                                                         t.setTotalAmount(BigDecimal.valueOf(doc.getDouble("total_amount")));
    //                                                     } 
    //                                                     // if(doc.get("total_amount").getClass() == Long.class){
    //                                                     //     t.setTotalAmount(BigDecimal.valueOf(doc.getLong("total_amount")));
    //                                                     // }
    //                                                     if(doc.get("total_amount").getClass() == Integer.class){
    //                                                         t.setTotalAmount(BigDecimal.valueOf((doc.getInteger("total_amount")).longValue()));
    //                                                     }
    //                                                     User payer = new User();
    //                                                     Document wp_doc = doc.get("who_paid", Document.class);
    //                                                     payer.setId(wp_doc.getString("id"));
    //                                                     payer.setName(wp_doc.getString("name"));
    //                                                     t.setWhoPaid(payer);

    //                                                     User recordUser = new User();
    //                                                     Document rec_doc = doc.get("recorded_by", Document.class);
    //                                                     recordUser.setId(rec_doc.getString("id"));
    //                                                     recordUser.setName(rec_doc.getString("name"));
    //                                                     t.setRecordedBy(recordUser);

    //                                                     ShareSplit shareSplit = new ShareSplit();
    //                                                     Document ss_doc = doc.get("shares_split", Document.class);
    //                                                     shareSplit.setId(ss_doc.getString("id"));
    //                                                     shareSplit.setName(ss_doc.getString("name"));
    //                                                     if(ss_doc.get("share_amount").getClass() == Double.class){
    //                                                         shareSplit.setShareAmount(BigDecimal.valueOf(ss_doc.getDouble("share_amount")));
    //                                                     } 
    //                                                     if(ss_doc.get("share_amount").getClass() == Long.class){
    //                                                         shareSplit.setShareAmount(BigDecimal.valueOf(ss_doc.getLong("share_amount")));
    //                                                     } 
    //                                                     if(ss_doc.get("share_amount").getClass() == Integer.class){
    //                                                         shareSplit.setShareAmount(BigDecimal.valueOf((ss_doc.getInteger("share_amount")).longValue()));
    //                                                     }
    //                                                     t.setWhoBorrowed(shareSplit);
    //                                                     t.setDate(doc.getLong("date"));
    //                                                     t.setRecordedDate(doc.getLong("recorded_date"));
    //                                                     return t;
    //                                                 }).toList();
    //     return transactions;
    // }
}
