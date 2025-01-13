import org.junit.jupiter.api.Test;
import java.util.HashSet;
import static org.junit.jupiter.api.Assertions.*;

public class SpreadsheetTest {
    @Test
    void testIsNumber(){
        Spreadsheet spreadsheet = new Spreadsheet(10,10);
        assertTrue(spreadsheet.isNumber("123"));
        assertTrue(spreadsheet.isNumber("-45.67"));
        assertFalse(spreadsheet.isNumber("12a"));

    }
    @Test
    void testEvalSimpleNumber(){
        Spreadsheet spreadsheet = new Spreadsheet(10,10);
        spreadsheet.set(0, 0, new SCell("123"));
        assertEquals("123", spreadsheet.eval(0, 0));
    }
    void testComplexFormula() throws Exception {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);
        spreadsheet.set(0, 0, new SCell("3"));
        spreadsheet.set(0, 1, new SCell("5"));
        spreadsheet.set(0, 2, new SCell("=A1+B1*2"));
        assertEquals("13.0", spreadsheet.eval(0, 2));
    }

    @Test
    void testCyclicReference() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);
        spreadsheet.set(0, 0, new SCell("=A1"));
        spreadsheet.set(0, 1, new SCell("=A0"));
        assertEquals("ERR_CYCLE", spreadsheet.eval(0, 0));
    }

    @Test
    void testInvalidFormula() {
        Spreadsheet spreadsheet = new Spreadsheet(10, 10);
        spreadsheet.set(0, 0, new SCell("=A1+"));
        assertEquals("ERR_FORM", spreadsheet.eval(0, 0));
    }

}
