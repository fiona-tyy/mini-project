package tfip.project.appbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(
      String toName, String toEmail, String lenderName, String lenderEmail, Double amount) {

        String emailSubject = String.format("Remember To Settle Up With %s", Util.toTitleCase(lenderName) );
        String text = String.format("Hi %s,\nYou have a total outstanding loan of $%.2f with %s (%s).\nRemember to settle up soon!\n\nNinjaSplit Team\n(This is an auto-generated email. Do not reply.)", Util.toTitleCase(toName),amount, Util.toTitleCase(lenderName), lenderEmail );
        
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("NinjaSplit Team<admin@fiona-tyy.com>");
        message.setTo(toEmail); 
        message.setSubject(emailSubject); 
        message.setText(text);
        emailSender.send(message);
        
    }

    // public void sendEmailWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException{

    //     MimeMessage message = emailSender.createMimeMessage();
     
    //     MimeMessageHelper helper = new MimeMessageHelper(message, true);
    
    //     helper.setFrom("noreply@baeldung.com");
    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(text);
        
    //     FileSystemResource file 
    //     = new FileSystemResource(new File(pathToAttachment));
    //     helper.addAttachment("Invoice", file);

    //     emailSender.send(message);

    // }
    
}
