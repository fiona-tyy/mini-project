package tfip.project.appbackend.configuration;

import java.io.IOException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FirebaseMessageConfig {
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    FirebaseApp firebaseApp() throws IOException {
        // FirebaseOptions options=null;
        // try {
        //     options = FirebaseOptions.builder()
        //       .setCredentials(GoogleCredentials.getApplicationDefault())
        //       .build();
        // } catch (IOException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
        // if(FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME) != null){
        //     return FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME);

        // }
        FirebaseApp firebaseApp;
        if(FirebaseApp.getApps().isEmpty()){
            firebaseApp = FirebaseApp.initializeApp(FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build());
        } else {
            firebaseApp = FirebaseApp.getApps().get(0);
        }
        return firebaseApp;


    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
        
    }
    
}
