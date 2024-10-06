import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final LexicalAnalyzer lexicalAnalyzer;
    private final Map<String, Object[]> varMap;

    public Parser(ArrayList<String> program){
        this.lexicalAnalyzer = new LexicalAnalyzer(program);
        this.varMap = new HashMap<>();
    }

    public void run() throws Exception {
        while(!lexicalAnalyzer.isEmpty()){
            statements();
        }
    }

    public void statements() throws Exception {
        lexicalAnalyzer.lex();
        CharClass tokenType = lexicalAnalyzer.getTokenType();

        if(tokenType != CharClass.IDENTIFIER){
            throw new Exception("Invalid statement on line " + lexicalAnalyzer.getLine());
        }

        Identifier identifier = identifier();

        if(identifier.equals("print")){//TODO: Generalize to all valid method calls
            methodCall();
        }

        lexicalAnalyzer.lex();
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals(";")){
            throw new Exception("Missing semicolon on line " + lexicalAnalyzer.getLine());
        }
    }

    public String str() throws Exception{

        StringBuilder lexeme = new StringBuilder();
        lexicalAnalyzer.lex();
        String lex = lexicalAnalyzer.getLexeme();

        while(lex.charAt(lex.length() - 1) != '"'){
            lexeme.append(lex);
            lexicalAnalyzer.lex();
            lex = lexicalAnalyzer.getLexeme();
        }

        if(lex.length() > 1){
            lexeme.append(lex, 0, lex.length() - 1);
        }

        lexicalAnalyzer.lex();
        return lexeme.toString();
    }

    public Object expression() throws Exception{
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        String lexeme = lexicalAnalyzer.getLexeme().strip();

        Object resolvedExpression;

        //TODO: Add mathematical and boolean operations
        if(tokenType == CharClass.DOUBLE_QUOTE){
            resolvedExpression = str();
        }else if(tokenType == CharClass.IDENTIFIER){
            resolvedExpression = identifier();
        }else if(tokenType == CharClass.DIGIT){
            resolvedExpression = intLiteral();
        }else{
            throw new Exception("Attempted to parse unimplemented expression on line " + lexicalAnalyzer.getLine());
        }

        return resolvedExpression;
    }

    public Integer intLiteral() throws Exception{
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        String lexeme = lexicalAnalyzer.getLexeme().strip();

        if(tokenType != CharClass.DIGIT){
            throw new Exception("Fake integer on line " + lexicalAnalyzer.getLine());
        }

        lexicalAnalyzer.lex();

        return Integer.parseInt(lexeme);
    }

    public Identifier identifier() throws Exception{
        Identifier identifier = new Identifier(lexicalAnalyzer.getLexeme().strip());
        lexicalAnalyzer.lex();

        return identifier;
    }

    public void methodCall() throws Exception{
        String lexeme = lexicalAnalyzer.getLexeme().strip();

        if(!lexeme.equals("(")){
            throw new Exception("Invalid method call on line " + lexicalAnalyzer.getLine());
        }

        lexicalAnalyzer.lex();

        Object o = expression();

        lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals(")")){
            throw new Exception("Invalid method call on line " + lexicalAnalyzer.getLine());
        }

        System.out.println(o.toString());
    }
}
