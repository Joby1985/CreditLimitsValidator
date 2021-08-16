package com.anz.credits;

/**
 * Base Exception class
 *
 * @author : Joby Job
 */
public class CreditValidatorException extends Exception{
    public CreditValidatorException(String message, Throwable t){
        super(message, t);
    }
    public CreditValidatorException(String message){
        this(message,null);
    }
}
