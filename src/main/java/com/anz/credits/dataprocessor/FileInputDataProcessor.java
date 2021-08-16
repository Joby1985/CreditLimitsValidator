package com.anz.credits.dataprocessor;

import com.anz.credits.CreditValidatorException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File based input implementation of InputDataProcessor
 *
 * Note: For simplicity, hard-coded the path of the input file.
 * Will need to pass this as a parameter
 * @author: Joby Job
 */
public class FileInputDataProcessor extends InputDataProcessor {
    private Logger logger = LogManager.getLogger(FileInputDataProcessor.class);

    String pathToCsv = "src/main/resources/creditsEntityInfo.csv";

    public FileInputDataProcessor(String pathToCsv){
        if(pathToCsv != null){
            this.pathToCsv = pathToCsv;
        }
    }

    @Override
    public List<String[]> getRawData() throws CreditValidatorException {

        List<String[]> output = new ArrayList<>();
        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(pathToCsv)) ))){
            String row = null;
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                output.add(data);
            }
        }
        catch(Exception e){
            logger.error("Error occurred while reading data from File {}", e.getMessage());
            throw new CreditValidatorException("An exception occurred while reading data from the source: "+e.getMessage());
        }
        if(logger.isDebugEnabled()) {
            logger.debug("Got row data from file: "+ Arrays.deepToString(output.toArray()));
        }
        return output;
    }
}
