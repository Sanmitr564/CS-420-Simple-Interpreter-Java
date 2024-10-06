import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    private final LexicalAnalyzer lexicalAnalyzer;
    private final Map<Identifier, Object> varMap;
    private final Map<Identifier, IdentifierType> reservedIdentifiers;

    public Parser(ArrayList<String> program){
        this.lexicalAnalyzer = new LexicalAnalyzer(program);
        this.varMap = new HashMap<>();
        this.reservedIdentifiers = new HashMap<>();
        reservedIdentifiers.put(new Identifier("print"), IdentifierType.METHOD);
        reservedIdentifiers.put(new Identifier("int"), IdentifierType.TYPE_DECLARATION);
        reservedIdentifiers.put(new Identifier("string"), IdentifierType.TYPE_DECLARATION);
        reservedIdentifiers.put(new Identifier("bool"), IdentifierType.TYPE_DECLARATION);
        reservedIdentifiers.put(new Identifier("if"), IdentifierType.CONDITIONAL);
        reservedIdentifiers.put(new Identifier("for"), IdentifierType.CONDITIONAL);
        reservedIdentifiers.put(new Identifier("while"), IdentifierType.CONDITIONAL);
        reservedIdentifiers.put(new Identifier("true"), IdentifierType.BOOLEAN);
        reservedIdentifiers.put(new Identifier("false"), IdentifierType.BOOLEAN);
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
        IdentifierType identifierType = reservedIdentifiers.get(identifier);
        if(identifierType == IdentifierType.METHOD){//TODO: Generalize to all valid method calls
            methodCall();
        }else if(identifierType == IdentifierType.TYPE_DECLARATION){
            declareVar();
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

    public Boolean bool() throws Exception{
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(lexeme.equals("true")){
            return true;
        }else if(lexeme.equals("false")){
            return false;
        }
        throw new Exception("Tried to initialize invalid boolean on line " + lexicalAnalyzer.getLine());
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

    public void declareVar() throws Exception {
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        Identifier name = identifier();
        Object var = switch (lexeme) {
            case "string" -> str();
            case "int" -> intLiteral();
            case "bool" -> bool();
            default -> throw new Exception("Unknown var type on line " + lexicalAnalyzer.getLine());
        };
        varMap.put(name, var);
        reservedIdentifiers.put(name, IdentifierType.VAR);
    }
}
