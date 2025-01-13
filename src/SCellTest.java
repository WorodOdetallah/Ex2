import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class SCellTest {
    @Test
    void testDetermineType(){
        SCell cell = new SCell("123");
        assertEquals(Ex2Utils.NUMBER, cell.getType());

        SCell cell2 = new SCell("=A1+2");
        assertEquals(Ex2Utils.FORM, cell2.getType());

        SCell cell3 = new SCell("Hello");
        assertEquals(Ex2Utils.TEXT, cell3.getType());
    }
    @Test
    void testComputeFormula() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);
        SCell cell = new SCell("=1+2*3");
        try {
            assertEquals(7.0, cell.computeFormula("=1+2*3", spreadsheet, new HashSet<>()));
        } catch (Exception e) {
            fail("Exception during computation: " + e.getMessage());
        }
    }


}
