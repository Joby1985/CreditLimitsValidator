package com.anz.credits;

/**
 * Amount related exception, eg. if invalid number is in the input.
 * @author : Joby Job
 */
public class CreditValidatorCorruptDataException extends CreditValidatorException {
    private static String INVALID_AMOUNT_Exception = "Inconsistent data. Specified parent credit entry is not defined in at least one of the records.";

    public CreditValidatorCorruptDataException(String message, Throwable t){
        super(message == null ?INVALID_AMOUNT_Exception : message,t);
    }
    public CreditValidatorCorruptDataException(String message){
        this(message == null ?INVALID_AMOUNT_Exception : message,null);
    }
    public CreditValidatorCorruptDataException(){
        this(INVALID_AMOUNT_Exception);
    }
    public CreditValidatorCorruptDataException(Throwable t){
        this(null,t);
    }
}
