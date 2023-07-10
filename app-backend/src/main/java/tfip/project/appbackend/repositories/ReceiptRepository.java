package tfip.project.appbackend.repositories;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@Repository
public class ReceiptRepository {
    

    @Autowired
    private AmazonS3 s3;

    public final String BUCKET = "ironbuck";
    public final String DOMAIN = "https://fiona-tyy.com/";
    
    public String uploadReceipt(String transactionId, MultipartFile file) throws IOException{

        Map<String, String> userData = new HashMap<>();
        userData.put("filename", file.getOriginalFilename());
        // userData.put("upload-date", (new Date()).toString());

      // Add object's metadata 
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setUserMetadata(userData);

        String key = "images/"+transactionId;

        PutObjectRequest putReq = new PutObjectRequest(BUCKET, key, 
            file.getInputStream(), metadata);
      // Make the file publically accessible
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);

        PutObjectResult result = s3.putObject(putReq);
        System.out.printf(">>> result: %s\n", result);


        return DOMAIN + key;

    }
}
