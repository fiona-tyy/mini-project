package tfip.project.appbackend.services;

public class TransactionException extends Exception{

    public TransactionException(){}

    public TransactionException(String msg){
        super(msg);
    }
    
}
