import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private final String inputFile;
    private final String outputFile;
    private final SymbolTable table;
    private int symbolValue;

    public Parser(String inputFile, String outputFile, SymbolTable table) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.table = table;
        symbolValue = 16;
    }

    public void firstPass() throws FileNotFoundException {
        Scanner reader = readFile(inputFile);
        int n = 0;
        while (reader.hasNextLine()) {
            String line = reader.nextLine().trim();
            if(line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            if(line.startsWith("(")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i <line.length() ; i++) {
                    char c = line.charAt(i);
                    if(c == ')') {
                        break;
                    }
                    builder.append(c);
                }
                table.addSymbol(builder.toString(), n);
                continue;
            }
            n++;
        }
    }

    private Scanner readFile(String input) throws FileNotFoundException {
        File file = new File(input);
        return new Scanner(file);
    }
}
