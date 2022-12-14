import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Parser {
    private final String inputFile;
    private final String outputFile;
    private final SymbolTable table;
    private final Code code;
    private int symbolValue;

    public Parser(String inputFile, String outputFile, SymbolTable table, Code code) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.table = table;
        this.code = code;
        symbolValue = 16;
    }

    public void parse() {
        try {
            this.firstPass();
            this.secondPass();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private void firstPass() throws FileNotFoundException {
        Scanner reader = readFile(inputFile);
        int n = 0;
        while (reader.hasNextLine()) {
            String line = reader.nextLine().trim();
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }
            if (line.startsWith("(")) {
                StringBuilder builder = new StringBuilder();
                for (int i = 1; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c == ')') {
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

    private void secondPass() throws IOException {
        Scanner reader = readFile(inputFile);
        PrintWriter writer = new PrintWriter(outputFile + ".hack", StandardCharsets.UTF_8);
        while (reader.hasNextLine()) {
            String line = reader.nextLine().trim().split(" ")[0];
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("(")) {
                continue;
            }
            if (line.startsWith("@")) {
                String binary = handleVariables(line);
                writer.println(binary);
                continue;
            }
            String binary = handleCommands(line);
            writer.println(binary);
        }
        writer.close();
    }

    private Scanner readFile(String input) throws FileNotFoundException {
        File file = new File(input);
        return new Scanner(file);
    }

    private String handleVariables(String variable) {
        String str = variable.substring(1);
        int num;
        if (str.matches("^\\d+$")) {
            num = Integer.parseInt(str);
        } else {
            Integer value = table.getSymbol(str);
            if (value == null) {
                table.addSymbol(str, symbolValue++);
            }
            num = table.getSymbol(str);
        }
        String binary = Integer.toBinaryString(num);

        if (binary.length() < 16) {
            String indent = "0".repeat(16 - binary.length());
            binary = indent + binary;
        }

        return binary;
    }

    private String handleCommands(String str) {
        String c = this.comp(str);
        String d = this.dest(str);
        String j = this.jump(str);

        String cc = code.comp(c);
        String dd = code.dest(d);
        String jj = code.jump(j);

        return "111" + cc + dd + jj;
    }

    private String dest(String str) {
        if (!str.contains("=")) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '=' || c == ';') {
                break;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private String comp(String str) {
        StringBuilder builder = new StringBuilder();
        int idx = str.indexOf("=");
        for (int i = idx + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == ';') {
                break;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private String jump(String str) {
        StringBuilder builder = new StringBuilder();
        int idx = str.indexOf(";");
        if (idx == -1) {
            return "null";
        }
        for (int i = idx + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            builder.append(c);
        }
        return builder.toString();
    }
}
