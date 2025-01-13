import java.util.HashSet;
import java.util.Set;

public class Spreadsheet {
    private SCell[][] cells;
    private int width;
    private int height;

    public Spreadsheet(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new SCell[width][height];
    }

    public void set(int x, int y, SCell cell) {
        cells[x][y] = cell;
    }

    public SCell get(int x, int y) {
        return cells[x][y];
    }

    public String eval(int x, int y) {
        return eval(x, y, new HashSet<>());
    }


    public String eval(int x, int y, Set<String> visited) {
        SCell cell = get(x, y);
        if (cell == null) return "ERR_EMPTY";

        String content = cell.getData();
        if (isNumber(content)) {
            return content;
        } else if (content.startsWith("=")) {
            if (visited.contains(x + "," + y)) {
                return "ERR_CYCLE"; // Cyclic reference detected
            }
            visited.add(x + "," + y);
            try {
                Double result = computeFormula(content, this, visited);
                return result != null ? result.toString() : "ERR_FORM";
            } catch (Exception e) {
                if (e.getMessage().equals("ERR_CYCLE")) {
                    return "ERR_CYCLE"; // Handle the specific ERR_CYCLE exception
                }
                return "ERR_FORM"; // Handle other formula errors
            }
        } else {
            return "ERR_TEXT"; // Invalid text
        }
    }

    public String[][] evalAll() {
        String[][] result = new String[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                try {
                    result[i][j] = eval(i, j, new HashSet<>()); // Fresh `visited` set for each cell
                } catch (Exception e) {
                    result[i][j] = "ERR_EVAL"; // Catch unexpected errors
                }
            }
        }
        return result;
    }



    public int[][] depth() {
        int[][] depthArray = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                depthArray[i][j] = calculateDepth(i, j, new HashSet<>());
            }
        }
        return depthArray;

    }

    private int calculateDepth(int x, int y, Set<String> visited) {
        SCell cell = get(x, y);
        if (cell == null || isNumber(cell.getData()) || isText(cell.getData())) {
            return 0;
        } else if (isForm(cell.getData())) {
            String content = cell.getData().substring(1);
            if (visited.contains(content)) {
                return -1; // ERR_CYCLE
            }
            visited.add(content);
            int maxDepth = 0;
            // Parse dependencies and calculate depth recursively
            return 1 + maxDepth;
        }
        return -1; // ERR_CYCLE
    }

    public int xCell(String c) {
        return c.charAt(0) - 'A';
    }

    public int yCell(String cellRef) {
        // Extract the numeric part of the cell reference and convert it to an index
        return Integer.parseInt(cellRef.replaceAll("[^0-9]", "")) - 1;
    }


    // Utility methods to check the type of content
    // In Spreadsheet class
    public boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private boolean isText(String text) {
        return !isNumber(text) && !isForm(text);
    }

    private boolean isForm(String text) {
        return text.startsWith("=");
    }

    // Compute a formula by evaluating its content
    private Double computeFormula(String formula, Spreadsheet spreadsheet, Set<String> visited) throws Exception {
        if (!formula.startsWith("=")) return null;

        String expression = formula.substring(1).trim();
        if (expression.matches("[A-Z]+[0-9]+")) {
            if (visited.contains(expression)) throw new Exception("ERR_CYCLE");
            visited.add(expression);

            int x = spreadsheet.xCell(expression);
            int y = spreadsheet.yCell(expression);
            String cellValue = spreadsheet.eval(x, y, visited);
            if (cellValue.equals("ERR_CYCLE") || cellValue.equals("ERR_FORM")) throw new Exception(cellValue);
            return Double.parseDouble(cellValue);
        }

        try {
            // Use Java's scripting engine to evaluate simple math expressions
            javax.script.ScriptEngine engine = new javax.script.ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(expression);

            // Convert result to Double (handle both Integer and Double)
            if (result instanceof Integer) {
                return ((Integer) result).doubleValue();
            } else if (result instanceof Double) {
                return (Double) result;
            } else {
                return null; // Invalid formula
            }
        } catch (Exception e) {
            return null; // Invalid formula
        }
    }

    private String replaceReferencesWithValues(String expression, Spreadsheet spreadsheet, Set<String> visited) throws Exception {
        StringBuilder parsedExpression = new StringBuilder();
        String[] tokens = expression.split("(?=[-+*/()])|(?<=[-+*/()])"); // Split by operators

        for (String token : tokens) {
            token = token.trim();
            if (token.matches("[A-Z]+[0-9]+")) { // If token is a cell reference
                if (visited.contains(token)) throw new Exception("ERR_CYCLE"); // Detect cycles
                visited.add(token);

                int x = xCell(token);
                int y = yCell(token);
                String cellValue = spreadsheet.eval(x, y, visited);

                if (cellValue.equals("ERR_CYCLE") || cellValue.equals("ERR_FORM")) throw new Exception(cellValue);

                parsedExpression.append(cellValue); // Replace reference with its value
            } else {
                parsedExpression.append(token); // Append non-reference tokens as-is
            }
        }

        return parsedExpression.toString();
    }

}
