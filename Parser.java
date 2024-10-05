import java.util.ArrayList;

public class Parser {
    private final LexicalAnalyzer lexicalAnalyzer;

    public Parser(ArrayList<String> program){
        this.lexicalAnalyzer = new LexicalAnalyzer(program);
    }

    public void run() throws Exception {
        while(!lexicalAnalyzer.isEmpty()){
            statements();
        }
    }

    public void statements() throws Exception {
        lexicalAnalyzer.lex();
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(tokenType != CharClass.IDENTIFIER){
            throw new Exception("Invalid statement on line " + lexicalAnalyzer.getLine());
        }

        //TODO: Generalize to statement later
        if(lexeme.equals("print")){
            lexicalAnalyzer.lex();
            lexeme = lexicalAnalyzer.getLexeme().strip();

            if(!lexeme.equals("(")){
                throw new Exception("Invalid method call on line " + lexicalAnalyzer.getLine());
            }

            String s = str();   //TODO: Replace with more general method call

            lexicalAnalyzer.lex();
            lexeme = lexicalAnalyzer.getLexeme().strip();
            if(!lexeme.equals(")")){
                throw new Exception("Invalid method call on line " + lexicalAnalyzer.getLine());
            }

            System.out.println(s);
        }

        lexicalAnalyzer.lex();
        lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals(";")){
            throw new Exception("Missing semicolon on line " + lexicalAnalyzer.getLine());
        }
    }

    public String str() throws Exception{
        lexicalAnalyzer.lex();
        StringBuilder lexeme = new StringBuilder(lexicalAnalyzer.getLexeme().strip());

        if(!lexeme.toString().equals("\"")){
            throw new Exception("Invalid string on line " + lexicalAnalyzer.getLine());
        }

        lexeme = new StringBuilder();
        lexicalAnalyzer.lex();
        String lex = lexicalAnalyzer.getLexeme();

        while(lex.charAt(lex.length() - 1) != '"'){
            lexeme.append(lex);
            lexicalAnalyzer.lex();
            lex = lexicalAnalyzer.getLexeme();
        }

        return lexeme.toString();
    }

    public String expression() throws Exception{
        lexicalAnalyzer.lex();
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        String lexeme = lexicalAnalyzer.getLexeme().strip();



        return "";
    }
}
