package com.anz.credits;

import com.anz.credits.dataprocessor.FileInputDataProcessor;
import com.anz.credits.dataprocessor.InputDataProcessor;
import com.anz.credits.model.CreditEntity;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;

/**
 * Junit tests
 * @author Joby Job
 */
public class CreditLimitsValidatorTest {

    @Test
    public void testValidateAndReportCreditEntities1() throws CreditValidatorException, IOException {
        InputDataProcessor processor = new FileInputDataProcessor("src/test/resources/creditEntities1.csv");
        List<CreditEntity> creditEntities = new ArrayList<>();
        List<String> result = CreditLimitsValidator.validateAndReportCreditEntities(processor);
        int totResults = result.size();
        Assertions.assertTrue(totResults == 2, "Expected 5 results, but found "+totResults);
        Assertions.assertEquals("Entities: A/B/C/D/:\n" +
                "\tNo limit breaches",result.get(0));
        Assertions.assertEquals("Entities: E/F/:\n" +
                "\tLimit breach at \n" +
                "\t\tE (limit = 200.0, direct utilization = 150.0, combined utilization = 230.0\n",result.get(1));
    }

    @Test
    public void testValidateAndReportCreditEntities2() throws CreditValidatorException, IOException {
        InputDataProcessor processor = new FileInputDataProcessor("src/test/resources/creditEntities2.csv");
        List<CreditEntity> creditEntities = new ArrayList<>();
        List<String> result = CreditLimitsValidator.validateAndReportCreditEntities(processor);
        int totResults = result.size();
        Assertions.assertTrue(totResults == 2, "Expected 5 results, but found "+totResults);
        Assertions.assertEquals("Entities: A/B/C/D/:\n" +
                "\tLimit breach at \n" +
                "\t\tC (limit = 40.0, direct utilization = 70.0, combined utilization = 70.0\n" +
                "\t\tB (limit = 90.0, direct utilization = 10.0, combined utilization = 110.0\n" +
                "\t\tA (limit = 100.0, direct utilization = 90.0, combined utilization = 200.0\n",result.get(0));
        Assertions.assertEquals("Entities: E/F/:\n" +
                "\tLimit breach at \n" +
                "\t\tE (limit = 200.0, direct utilization = 150.0, combined utilization = 230.0\n",result.get(1));
    }

    @Test
    public void testValidateAndReportCreditEntitiesAnyOrderOfColumns() throws CreditValidatorException, IOException {
        InputDataProcessor processor = new FileInputDataProcessor("src/test/resources/creditEntities3.csv");
        List<CreditEntity> creditEntities = new ArrayList<>();
        List<String> result = CreditLimitsValidator.validateAndReportCreditEntities(processor);
        int totResults = result.size();
        Assertions.assertTrue(totResults == 2, "Expected 2 results, but found "+totResults);
        Assertions.assertEquals("Entities: A/B/C/D/:\n" +
                "\tLimit breach at \n" +
                "\t\tC (limit = 40.0, direct utilization = 70.0, combined utilization = 70.0\n" +
                "\t\tB (limit = 90.0, direct utilization = 10.0, combined utilization = 110.0\n" +
                "\t\tA (limit = 100.0, direct utilization = 90.0, combined utilization = 200.0\n",result.get(0));
        Assertions.assertEquals("Entities: E/F/:\n" +
                "\tLimit breach at \n" +
                "\t\tE (limit = 200.0, direct utilization = 150.0, combined utilization = 230.0\n",result.get(1));
    }
    @Test
    public void testValidateAndReportCreditEntitiesAnyOrderOfColumnsAndRows() throws CreditValidatorException, IOException {
        InputDataProcessor processor = new FileInputDataProcessor("src/test/resources/creditEntities5.csv");
        List<CreditEntity> creditEntities = new ArrayList<>();
        List<String> result = CreditLimitsValidator.validateAndReportCreditEntities(processor);
        int totResults = result.size();
        Assertions.assertTrue(totResults == 2, "Expected 2 results, but found "+totResults);
        Assertions.assertEquals("Entities: A/B/C/D/:\n" +
                "\tLimit breach at \n" +
                "\t\tC (limit = 40.0, direct utilization = 70.0, combined utilization = 70.0\n" +
                "\t\tB (limit = 90.0, direct utilization = 10.0, combined utilization = 110.0\n" +
                "\t\tA (limit = 100.0, direct utilization = 90.0, combined utilization = 200.0\n",result.get(0));
        Assertions.assertEquals("Entities: E/F/:\n" +
                "\tLimit breach at \n" +
                "\t\tE (limit = 200.0, direct utilization = 150.0, combined utilization = 230.0\n",result.get(1));
    }
    @Test
    public void testValidateAndReportCreditEntitiesCorruptedDataException1() throws CreditValidatorException, IOException {
        // At least 1 row has records < 4 tuples
        InputDataProcessor processor = new FileInputDataProcessor("src/test/resources/creditEntities4.csv");
        List<CreditEntity> creditEntities = new ArrayList<>();
        CreditValidatorException thrown = Assertions.assertThrows(CreditValidatorException.class,
                () -> CreditLimitsValidator.validateAndReportCreditEntities(processor));
        Assertions.assertEquals(CreditValidatorCorruptDataException.class,thrown.getClass());
    }

    @Test
    public void testValidateAndReportCreditEntitiesCorruptedDataException2() throws CreditValidatorException, IOException {
        // At least 1 parent record is not defined in the input data.
        InputDataProcessor processor = new FileInputDataProcessor("src/test/resources/creditEntities6.csv");
        List<CreditEntity> creditEntities = new ArrayList<>();
        CreditValidatorException thrown = Assertions.assertThrows(CreditValidatorException.class,
                () -> CreditLimitsValidator.validateAndReportCreditEntities(processor));
        Assertions.assertEquals(CreditValidatorCorruptDataException.class,thrown.getClass());
    }
}
