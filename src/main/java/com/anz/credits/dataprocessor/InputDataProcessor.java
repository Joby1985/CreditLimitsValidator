package com.anz.credits.dataprocessor;

import com.anz.credits.CreditValidatorCorruptDataException;
import com.anz.credits.CreditValidatorException;
import com.anz.credits.CreditValidatorInvalidAmountException;
import com.anz.credits.model.CreditEntity;
import com.anz.credits.model.CreditEntitySet;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

/**
 * @Author: Joby Job
 *
 * Abstract base type to implement data processor.
 *
 * getRawData() has to be implemented by the concrete implementations like FileInputDataProcessor.
 * getNodeData() returns the read data as Nodes.
 */
public abstract class InputDataProcessor{
    private static Logger logger = LogManager.getLogger(FileInputDataProcessor.class);

    private final String HEADER_ITEM_ENTITY="entity";
    private final String HEADER_ITEM_PARENT="parent";
    private final String HEADER_ITEM_LIMIT="limit";
    private final String HEADER_ITEM_UTILIZ="utilization";

    private int INDEX_ENTITY = 0;
    private int INDEX_PARENT = 1;
    private int INDEX_LIMIT = 2;
    private int INDEX_UTILIZ = 3;

    // This structure is used as a temporary store
    private CreditEntitySet<CreditEntity> tmpAllCreditEntities = new CreditEntitySet<>();

    // The actual node details storage
    private List<CreditEntity> nodesDetails;

    /**
     * Get separate hierarchy of Credit Entity Nodes.
     *  Each item in this list will be the root parent with no parent.
     *      And we have children list for each of those nodes using which we can traverse.
     * @return
     * @throws CreditValidatorException
     */
    public final List<CreditEntity> getNodeData() throws CreditValidatorException {
        if (nodesDetails == null){
            nodesDetails = loadNodeData();
        }
        return nodesDetails;
    }

    /**
     * Ensure that the input data is sane and not corrupted. And then read them into nodes.
     *
     * @return
     * @throws CreditValidatorException
     */
    private List<CreditEntity> loadNodeData() throws CreditValidatorException{
        List<String[]> rawData = getRawData();
        List<CreditEntity> returnData = new ArrayList<>();
        boolean isHeaderData = false;
		logger.debug("called");
        for (int i=0; i< rawData.size(); i++){
            String[] data = rawData.get(i);
            int totItems = 0;
            // Ignore totally blank rows.. consider only totItems > 0
            if(data != null && (totItems = data.length) > 0){
                String row = Arrays.toString(data);
                if (totItems < 4){ // We don't care if a line has more data than required.
                    throw new CreditValidatorCorruptDataException("Expected 4 tuples of data in the row: "+row+", but found only "+totItems);
                }
                //probably header data.
                if(i == 0){
                    isHeaderData = checkForAndDetermineHeaderIndices(data);
                }
                else{
                    isHeaderData = false;
                }
                // Header is optional. If it is not a header, then only process as data.
                // Assuming that if a header is present, the 3rd column represents limit.
                if(!isHeaderData){
                    boolean isChildRecord = false;
                    String parentNodeName = data[INDEX_PARENT];
                    CreditEntity parentCreditEntity = null;
                    // if there is a mention of parentNode name in the data, then it is child record.
                    // if so, we need to fetch the parent
                    if(isChildRecord = (parentNodeName != null && !"".equals(parentNodeName.trim()))){
                        parentCreditEntity = tmpAllCreditEntities.getByName(parentNodeName);
                    }
                    Double limit = getAmountSanitized(data[INDEX_LIMIT], row);
                    Double utilization = getAmountSanitized(data[INDEX_UTILIZ], row);
                    CreditEntity creditEntity = new CreditEntity(data[INDEX_ENTITY], parentNodeName, parentCreditEntity, limit, utilization);
                    if(parentCreditEntity != null){
                        parentCreditEntity.addChild(creditEntity);
                        if(logger.isDebugEnabled()) {
                            logger.debug("Added child : {} to the parent credit entry {}",creditEntity, parentCreditEntity);
                        }
                    }
                    else{
                        if (isChildRecord){
                            logger.warn("No parent record with ID {} found for {} to the parent credit entry {}. Will need to do final check.",creditEntity, parentCreditEntity);
                        }
                        else{// In return structure, add only parent nodes.
                            returnData.add(creditEntity);
                            if(logger.isDebugEnabled()) {
                                logger.debug("Adding a new root record {}",creditEntity);
                            }
                        }
                    }
                    tmpAllCreditEntities.add(creditEntity);
                }
            }
        }
        reconcileAndFixNullParentReferencesOfChildRecords();
        //Finally clear the temp storage
        tmpAllCreditEntities.clear();;
        tmpAllCreditEntities = null;
        return returnData;
    }

    /**
     * It might be that the records come in any order. The parent definition might be appearing
     * later. We need to fix the dangling parent references in this case.
     *
     * @throws CreditValidatorException
     */
    private void reconcileAndFixNullParentReferencesOfChildRecords() throws CreditValidatorException{
        for(CreditEntity entity : tmpAllCreditEntities){
            //We need to fix such records. The child record appeared before parent record when it was read from the source.
            if(entity.getParent() == null && entity.getParentName() != null && !"".equals(entity.getParentName().trim())){
                CreditEntity parent = tmpAllCreditEntities.getByName(entity.getParentName());
                if(parent == null){
                    throw new CreditValidatorCorruptDataException("Inconsistent data. Specified parent credit entry "+entity.getParentName()+" is not defined.");
                }
                parent.addChild(entity);
            }
        }
    }

    /**
     * With the row data passed in (typically for row 0), checks if the values correspond to header values
     * and if so, set the corresponding column indices.
     * If no header info found, then default as defined assumes
     *
     * @param data
     * @return
     */
    private boolean checkForAndDetermineHeaderIndices(String[] data) {
        boolean isHeaderData = false;
        for(int colIndx=0; colIndx < 4; colIndx++ ){
            String colData = data[colIndx];
            switch (colData){
                case HEADER_ITEM_ENTITY:
                    INDEX_ENTITY = colIndx;
                    isHeaderData = true;
                    break;
                case HEADER_ITEM_PARENT:
                    INDEX_PARENT = colIndx;
                    isHeaderData = true;
                    break;
                case HEADER_ITEM_LIMIT:
                    INDEX_LIMIT = colIndx;
                    isHeaderData = true;
                    break;
                case HEADER_ITEM_UTILIZ:
                    INDEX_UTILIZ = colIndx;
                    isHeaderData = true;
                    break;
            }
        }
        return isHeaderData;
    }

    /**
     * Sanitize the input amount string.
     *    Any empty/blank values would be interpreted as 0.0
     *    Also validate that the string represent a Number/Double value.
     * @param amount
     * @param rowString
     * @return
     * @throws CreditValidatorException
     */
    private Double getAmountSanitized(String amount, String rowString) throws CreditValidatorException {
        Double retAmount = null;
        if(amount == null || "".equals(amount.trim())){
            retAmount = 0.0;
        }
        else{
            try{
                retAmount = Double.parseDouble(amount);
            }catch(Exception e){
                throw new CreditValidatorInvalidAmountException();
            }
        }
        return retAmount;
    }

    /**
     * Method to get the input data from an external source: file / external API
     * @return
     * @throws CreditValidatorException
     */
    public abstract List<String[]> getRawData() throws CreditValidatorException;
}
