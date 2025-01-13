import java.util.Set;

public class SCell implements Cell {
    private String content; // Raw data of the cell (e.g., "123", "Text", "=A1+2")
    private int type;       // Type of the cell: TEXT, NUMBER, FORM, ERR_CYCLE_FORM, etc.
    private int order;      // Order of dependency

    public SCell(String content) {
        this.content = content.trim();
        determineType();
    }

    // Determine the type of the cell based on its content
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

    // Check if the content is a valid number
    private boolean isNumber(String text) {
        try {
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check if the content is text
    public boolean isText(String text) {
        return !isNumber(text) && !isForm(text);
    }

    // Check if the content is a formula
    public boolean isForm(String text) {
        return text.startsWith("=");
    }

    // Compute the result of a formula
    public Double computeFormula(String formula, Spreadsheet spreadsheet, Set<String> visited) {
        if (!formula.startsWith("=")) return null; // Not a formula
        String expression = formula.substring(1).trim(); // Remove '='

        try {
            return evaluateExpression(expression, spreadsheet, visited);
        } catch (Exception e) {
            return null; // Handle errors (e.g., cyclic dependencies)
        }
    }

    // Evaluate a formula expression
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

        // Add your arithmetic operations handling logic here (e.g., +, -, *, /).

        throw new UnsupportedOperationException("Complex formulas not yet supported");
    }

    @Override
    public String getData() {
        return content;
    }

    @Override
    public void setData(String s) {
        this.content = s.trim();
        determineType();
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        this.type = t;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int t) {
        this.order = t;
    }
}
