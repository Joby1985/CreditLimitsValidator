package com.anz.credits;

import com.anz.credits.dataprocessor.FileInputDataProcessor;
import com.anz.credits.dataprocessor.InputDataProcessor;
import com.anz.credits.model.CreditEntity;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Actual validator class.
 *  It calls the InputDataProcessor, and iterates over the list of root nodes and in turn recursively traverses thru each child, grand child, ... nth child of the hierarchy and aggregates all violdations.
 * @author: Joby Job
 */
public class CreditLimitsValidator {
    private static Logger logger = LogManager.getLogger(CreditLimitsValidator.class);
    public static void main(String[] args){
        // We currently use File Input Data
        // File path can be provided as a command line argument.
        //      If not, default path "src/main/resources/creditsEntityInfo.csv" would be taken.
        InputDataProcessor processor = new FileInputDataProcessor(args.length > 0 ?args[0] : null);
        try{
            List<String> results  = validateAndReportCreditEntities(processor);
            results.forEach(System.out::println);
        }
        catch(CreditValidatorException e){
            logger.error("An error occurred while reading the file : {}",e.getMessage(),e);
            System.out.println("An error occurred while reading the file :"+e.getMessage());
        }
    }

    /**
     * Do the credit limits validation and return results in a List for reporting.
     *
     * @param processor
     * @return
     * @throws CreditValidatorException
     */
    public static List<String> validateAndReportCreditEntities(InputDataProcessor processor) throws CreditValidatorException{
        List<CreditEntity> creditEntities = processor.getNodeData();
        List<String> results = new ArrayList<>();
        for (CreditEntity creditEntity : creditEntities){
            StringBuilder entityInfo = new StringBuilder();
            entityInfo.append(creditEntity.getCreditEntityName());
            StringBuilder breachDetails = new StringBuilder();
            boolean valid = validateNode(creditEntity, entityInfo, breachDetails, true);

            if(valid){
                results.add("Entities: "+entityInfo.toString()+":\n\tNo limit breaches");
            }
            else{
                results.add("Entities: "+entityInfo.toString()+":\n\tLimit breach at \n"+breachDetails.toString());
            }
        }
        return results;
    }


    /**
     * Validate each node in each hierarchies.
     *  Note that we have each hierarchy of nodes in the return list when we invoke
     *      processor.getNodeData();
     *
     * @param creditEntity
     * @param entityInfo
     * @param breachDetails
     * @param validState
     * @return
     */
    private static boolean validateNode(CreditEntity creditEntity,
                                        StringBuilder entityInfo,
                                        StringBuilder breachDetails,
                                        boolean validState) throws CreditValidatorException{
        entityInfo.append("/");
        boolean thisEntityValidState = (creditEntity.getCumulativeUtilization() <= creditEntity.getLimit());
        validState = validState && thisEntityValidState;
        if (creditEntity.getChildren() != null){
            for(CreditEntity child : creditEntity.getChildren()){
                entityInfo.append(child.getCreditEntityName());
                thisEntityValidState = validateNode(child, entityInfo, breachDetails, validState);
                validState = validState && thisEntityValidState;
            }
        }
        if(!thisEntityValidState) {
            breachDetails.append("\t\t").append(creditEntity.getCreditEntityName()).append(" (limit = ")
                    .append(creditEntity.getLimit()).append(", direct utilization = ")
                    .append(creditEntity.getUtilization()).append(", combined utilization = ")
                    .append(creditEntity.getCumulativeUtilization()).append("\n");
            //Not throwing exception here, since we are interested in all possible Limit breaches in the tree at all levels.
            //throw new CreditValidatorLimitsBreachedException("Amount breached");
        }
        return validState;
    }
}
