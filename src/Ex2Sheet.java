import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Ex2Sheet implements Sheet {
    private SCell[][] table;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        Cell cell = (Cell) get(x, y);
        return cell != null ? cell.getData() : Ex2Utils.EMPTY_CELL;
    }

    @Override
    public SCell get(int x, int y) {
        return table[x][y];
    }

    @Override
    public SCell get(String cords) {
        cords = cords.trim().toUpperCase();
        if (cords.length() < 2) return null;
        char colChar = cords.charAt(0);
        String rowStr = cords.substring(1);

        try {
            int col = colChar - 'A';
            int row = Integer.parseInt(rowStr) - 1;
            if (isIn(col, row)) return table[col][row];
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    @Override
    public int width() {
        return table.length;
    }

    @Override
    public int height() {
        return table[0].length;
    }

    @Override
    public void set(int x, int y, String s) {
        SCell cell = new SCell(s);
        table[x][y] = cell;
        eval();
    }

    @Override
    public void eval() {
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                SCell cell = table[x][y];
                if (cell.getType() == Ex2Utils.FORM) {
                    String result = eval(x, y);
                    if (result.equals(Ex2Utils.ERR_CYCLE)) {
                        cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                    } else if (result.equals(Ex2Utils.ERR_FORM)) {
                        cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                    }
                }
            }
        }
    }

    @Override
    public boolean isIn(int x, int y) {
        return x >= 0 && y >= 0 && x < width() && y < height();
    }

    @Override
    public int[][] depth() {
        int[][] depthArray = new int[width()][height()];
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                depthArray[x][y] = calculateDepth(x, y, new HashSet<>());
            }
        }
        return depthArray;
    }

    private int calculateDepth(int x, int y, Set<String> visited) {
        if (!isIn(x, y)) return Ex2Utils.ERR;
        SCell cell = table[x][y];
        if (cell == null || cell.getType() == Ex2Utils.TEXT || cell.getType() == Ex2Utils.NUMBER) {
            return 0;
        } else if (cell.getType() == Ex2Utils.FORM) {
            String formula = cell.getData().substring(1);
            if (visited.contains(formula)) return Ex2Utils.ERR_CYCLE_FORM;
            visited.add(formula);
            // Calculate max depth
            return 1; // Replace this with proper dependency depth calculation logic
        }
        return Ex2Utils.ERR_CYCLE_FORM;
    }

    @Override
    public void load(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                String data = parts[2].trim();
                set(x, y, data);
            }
        }
        reader.close();
        eval();
    }

    @Override
    public void save(String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                SCell cell = get(x, y);
                if (cell != null && !cell.getData().isEmpty()) {
                    writer.write(x + "," + y + "," + cell.getData());
                    writer.newLine();
                }
            }
        }
        writer.close();
    }

    @Override
    public String eval(int x, int y) {
        SCell cell = get(x, y);
        if (cell == null) return Ex2Utils.EMPTY_CELL;
        return cell.getData();
    }
}
