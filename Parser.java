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
        if(identifierType == IdentifierType.METHOD){
            methodCall(identifier);
        }else if(identifierType == IdentifierType.TYPE_DECLARATION){
            declareVar(identifier);
        }else if(identifierType == IdentifierType.VAR){
            assignVar(identifier);
        }

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

        Object term = term();

        //TODO: Add mathematical and boolean operations


        return term;
    }

    public Object term() throws Exception{
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        Object resolvedTerm;

        if(tokenType == CharClass.DOUBLE_QUOTE){
            resolvedTerm = str();
        }else if(tokenType == CharClass.IDENTIFIER){
            Identifier identifier = identifier();
            if(!reservedIdentifiers.containsKey(identifier)){
                throw new Exception("Unknown identifier on line " + lexicalAnalyzer.getLine());
            }
            IdentifierType identifierType = reservedIdentifiers.get(identifier);
            if(identifierType == IdentifierType.VAR){
                resolvedTerm = varMap.get(identifier);
            }else if(identifierType == IdentifierType.BOOLEAN){
                resolvedTerm = bool(identifier);
            }else if(identifierType == IdentifierType.METHOD){
                throw new Exception("Methods as expression arguments not yet implemented on line " + lexicalAnalyzer.getLine());
            }else{
                throw new Exception("Invalid identifier in expression on line " + lexicalAnalyzer.getLine());
            }

        }else if(tokenType == CharClass.DIGIT){
            resolvedTerm = intLiteral();
        }else{
            throw new Exception("Attempted to parse unimplemented expression on line " + lexicalAnalyzer.getLine());
        }
        return resolvedTerm;
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

    public Boolean bool(Identifier identifier) throws Exception{
        if(identifier.equals("true")){
            return true;
        }else if(identifier.equals("false")){
            return false;
        }
        throw new Exception("Tried to initialize invalid boolean on line " + lexicalAnalyzer.getLine());
    }

    public void methodCall(Identifier identifier) throws Exception{ //TODO: Generalize
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
        lexicalAnalyzer.lex();
        System.out.println(o.toString());
    }

    public void declareVar(Identifier type) throws Exception {
        if(lexicalAnalyzer.getTokenType() != CharClass.IDENTIFIER){
            throw new Exception("Improper variable initialization on line " + lexicalAnalyzer.getLine());
        }
        Identifier name = identifier();

        if(reservedIdentifiers.containsKey(name)){
            throw new Exception("Tried to create variable from reserved identifier on line " + lexicalAnalyzer.getLine());
        }

        if(!lexicalAnalyzer.getLexeme().strip().equals("=")){
            throw new Exception("Must initialize variable on line " + lexicalAnalyzer.getLine());
        }

        lexicalAnalyzer.lex();

        Object var = expression();
        if(type.equals("string") && !(var instanceof String)){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLine());
        }else if(type.equals("int") && !(var instanceof Integer)){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLine());
        }else if(type.equals("bool") && !(var instanceof Boolean)){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLine());
        }
        varMap.put(name, var);
        reservedIdentifiers.put(name, IdentifierType.VAR);
    }

    public void assignVar(Identifier name) throws Exception{
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals("=")){
            throw new Exception("Improper variable assignment on line " + lexicalAnalyzer.getLine());
        }

        lexicalAnalyzer.lex();
        Object var = expression();
        if(!var.getClass().equals(varMap.get(name).getClass())){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLine());
        }

        varMap.put(name, var);

    }
}
