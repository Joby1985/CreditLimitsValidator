package com.anz.credits;

/**
 * Base Exception class
 *
 * @author : Joby Job
 */
public class CreditValidatorLimitsBreachedException extends CreditValidatorException{
    public CreditValidatorLimitsBreachedException(String message, Throwable t){
        super(message, t);
    }
    public CreditValidatorLimitsBreachedException(String message){
        this(message,null);
    }
}
