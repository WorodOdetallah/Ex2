import java.util.Set;

public class CellImpl {
    private String content; // Raw data of the cell (e.g., "123", "Text", "=A1+2")
    private int type;       // Type of the cell: TEXT, NUMBER, FORM, ERR_CYCLE_FORM, etc.

    public CellImpl(String content) {
        this.content = content.trim();
        determineType();
    }

    private void determineType() {
        if (isNumber(content)) {
            type = Ex2Utils.NUMBER;
        } else if (content.startsWith("=")) {
            type = Ex2Utils.FORM;
        } else if (!content.isEmpty()) {
            type = Ex2Utils.TEXT;
        } else {
            type = Ex2Utils.ERR_FORM_FORMAT;
        }
    }


    private boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isText(String text) {
        return !isNumber(text) && !isForm(text);
    }

    public boolean isForm(String text) {
        return text.startsWith("=");
    }

    public Double computeFormula(String formula, Spreadsheet spreadsheet, Set<String> visited) {
        if (!formula.startsWith("=")) return null; // Not a formula
        String expression = formula.substring(1).trim(); // Remove '='

        try {
            return evaluateExpression(expression, spreadsheet, visited);
        } catch (Exception e) {
            return null; // Handle errors (e.g., cyclic dependencies)
        }
    }

    private Double evaluateExpression(String expression, Spreadsheet spreadsheet, Set<String> visited) throws Exception {
        if (visited.contains(expression)) throw new Exception("ERR_CYCLE"); // Circular reference

        // Parse references to other cells (e.g., "A1", "B2")
        if (expression.matches("[A-Z]+[0-9]+")) {
            int x = spreadsheet.xCell(expression);
            int y = spreadsheet.yCell(expression);
            visited.add(expression); // Track visited cells
            String result = spreadsheet.eval(x, y, visited);
            return result != null ? Double.parseDouble(result) : null;
        }

        // Handle arithmetic operations (+, -, *, /)
        // Parse and evaluate using a stack-based approach or a library like `javax.script.ScriptEngine`.

        // Example: Basic implementation for simple cases
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            return evaluateExpression(parts[0].trim(), spreadsheet, visited)
                    + evaluateExpression(parts[1].trim(), spreadsheet, visited);
        }

        // Add other operators (-, *, /) as needed.
        throw new UnsupportedOperationException("Complex formulas not yet supported");
    }




    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content.trim();
        determineType();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
