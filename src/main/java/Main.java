public class Main {
    public static void main(String[] args) {
        String input = args[0];
        if(input.isBlank() || !input.endsWith(".asm")) {
            throw new IllegalArgumentException();
        }
        String output = input.substring(0,input.length()-4);

        SymbolTable table = new SymbolTable();
        Code code = new Code();
        Parser parser = new Parser(input,output,table,code);
        parser.parse();
    }
}