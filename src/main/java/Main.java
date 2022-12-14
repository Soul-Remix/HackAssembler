public class Main {
    public static void main(String[] args) {
        SymbolTable table = new SymbolTable();
        Parser parser = new Parser(args[0],args[0],table);
    }
}