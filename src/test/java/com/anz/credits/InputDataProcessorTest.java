package com.anz.credits;

import com.anz.credits.dataprocessor.InputDataProcessor;
import com.anz.credits.model.CreditEntity;
import com.anz.credits.model.CreditEntitySet;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;

/**
 * Unit test for Input Data processor
 * @author Joby Job
 */
public class InputDataProcessorTest {
    @Test
    public void testGetNodeData() throws CreditValidatorException{
        InputDataProcessor dataProcessor = Mockito.mock(
                InputDataProcessor.class,
                Mockito.CALLS_REAL_METHODS);
        // Set private field in the mock.
        try{
            FieldUtils.writeField(dataProcessor, "tmpAllCreditEntities", new CreditEntitySet<CreditEntity>(), true);
            FieldUtils.writeField(dataProcessor, "INDEX_ENTITY", 0, true);
            FieldUtils.writeField(dataProcessor, "INDEX_PARENT", 1, true);
            FieldUtils.writeField(dataProcessor, "INDEX_LIMIT", 2, true);
            FieldUtils.writeField(dataProcessor, "INDEX_UTILIZ", 3, true);
        }
        catch (IllegalAccessException e){
            throw new CreditValidatorException("Internal error - "+e.getMessage());
        }
        List<String[]> mocked = Arrays.asList(new String[][]{{"A","","100","0"},{"B","A","90","10"},{"C","B","40","20"},{"D","B","40","30"}});
        when(dataProcessor.getRawData()).thenReturn(mocked);
        List<CreditEntity> nodes = dataProcessor.getNodeData();
        Assertions.assertEquals(1,nodes.size());
        CreditEntity entity = nodes.get(0);
        Assertions.assertEquals(1,entity.getChildren().size());
        entity = entity.getChildren().get(0);
        Assertions.assertEquals(2,entity.getChildren().size());
    }
}
