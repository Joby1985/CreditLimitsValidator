package com.anz.credits;

/**
 * Amount related exception, eg. if invalid number is in the input.
 * @author : Joby Job
 */
public class CreditValidatorInvalidAmountException extends CreditValidatorException {
    private static String INVALID_AMOUNT_Exception = "There is invalid amount in the input.";

    public CreditValidatorInvalidAmountException(String message, Throwable t){
        super(message == null ?INVALID_AMOUNT_Exception : message,t);
    }
    public CreditValidatorInvalidAmountException(String message){
        this(message == null ?INVALID_AMOUNT_Exception : message,null);
    }
    public CreditValidatorInvalidAmountException(){
        this(INVALID_AMOUNT_Exception);
    }
    public CreditValidatorInvalidAmountException(Throwable t){
        this(null,t);
    }
}
