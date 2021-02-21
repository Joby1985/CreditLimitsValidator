package com.anz.credits.dataprocessor;

import com.anz.credits.CreditValidatorException;
import com.anz.credits.model.CreditEntity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * File based input implementation of InputDataProcessor
 *
 * Note: For simplicity, hard-coded the path of the input file.
 * Will need to pass this as a parameter
 * @author: Joby Job
 */
public class FileInputDataProcessor extends InputDataProcessor {
    String pathToCsv = "src/main/resources/creditsEntityInfo.csv";

    public FileInputDataProcessor(String pathToCsv){
        if(pathToCsv != null){
            this.pathToCsv = pathToCsv;
        }
    }

    @Override
    public List<String[]> getRawData() throws CreditValidatorException {


        List<String[]> output = new ArrayList<>();
        try (BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv))){
            String row = null;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                output.add(data);
            }
        }
        catch(Exception e){
            throw new CreditValidatorException("And exception occurred while reading data from the source");
        }
        return output;
    }
}
