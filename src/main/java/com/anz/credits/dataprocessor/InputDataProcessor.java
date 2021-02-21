package com.anz.credits.dataprocessor;

import com.anz.credits.CreditValidatorException;
import com.anz.credits.CreditValidatorInvalidAmountException;
import com.anz.credits.model.CreditEntity;
import com.anz.credits.model.CreditEntitySet;

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
    // This structure is used as a temporary store
    private CreditEntitySet<CreditEntity> allCreditEntities = new CreditEntitySet<>();
    private final String HEADER_ITEM_ENTITY="entity";
    private final String HEADER_ITEM_PARENT="parent";
    private final String HEADER_ITEM_LIMIT="limit";
    private final String HEADER_ITEM_UTILIZ="utilization";

    private int INDEX_ENTITY = 0;
    private int INDEX_PARENT = 1;
    private int INDEX_LIMIT = 2;
    private int INDEX_UTILIZ = 3;

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
        for (int i=0; i< rawData.size(); i++){
            String[] data = rawData.get(i);
            if(data != null){
                int totItems = data.length;
                String row = Arrays.toString(data);
                if (totItems < 4){ // We don't care if a line has more data than required.
                    throw new CreditValidatorException("Expected 4 tuples of data in the row: "+row+", but found only "+totItems);
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
                    String parentNodeName = data[INDEX_PARENT];
                    CreditEntity parentCreditEntity = null;
                    if(parentNodeName != null && !"".equals(parentNodeName.trim())){
                        parentCreditEntity = allCreditEntities.getByName(parentNodeName);
                    }
                    Double limit = getAmountSanitized(data[INDEX_LIMIT], row);
                    Double utilization = getAmountSanitized(data[INDEX_UTILIZ], row);
                    CreditEntity creditEntity = new CreditEntity(data[INDEX_ENTITY], parentCreditEntity, limit, utilization);
                    if(parentCreditEntity != null){
                        parentCreditEntity.addChild(creditEntity);
                    }
                    else{ // In return structure, add only parent nodes.
                        returnData.add(creditEntity);
                    }
                    allCreditEntities.add(creditEntity);
                }
            }
        }/*
        Iterator<CreditEntity> allEntities = allCreditEntities.iterator();
        while (allEntities.hasNext()){
            System.out.println(allEntities.next());
        }*/
        return returnData;
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
