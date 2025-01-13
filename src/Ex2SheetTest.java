import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
public class Ex2SheetTest {
    @Test
   void  testSetAndGet(){
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "123");
        assertEquals("123", sheet.get(0, 0).getData());

    }
    @Test
    void testSaveAndLoad() throws IOException {
        Ex2Sheet sheet = new Ex2Sheet(5, 5);
        sheet.set(0, 0, "123");
        sheet.set(1, 1, "=A1+2");

        String fileName = "testSheet.csv";
        sheet.save(fileName);

        Ex2Sheet loadedSheet = new Ex2Sheet();
        loadedSheet.load(fileName);
        assertEquals("123", loadedSheet.get(0, 0).getData());
        assertEquals("=A1+2", loadedSheet.get(1, 1).getData());
    }

}
