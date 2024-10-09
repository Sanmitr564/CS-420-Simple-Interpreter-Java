import java.util.*;

public class Parser {
    private final LexicalAnalyzer lexicalAnalyzer;
    private final ScopeList<Object> varMap;
    private final ScopeList<IdentifierType> reservedIdentifiers;

    public Parser(ArrayList<String> program) throws Exception {
        this.lexicalAnalyzer = new LexicalAnalyzer(program);
        this.varMap = new ScopeList<>();
        varMap.lockInitialScope();
        varMap.increaseScope();

        this.reservedIdentifiers = new ScopeList<>();
        reservedIdentifiers.put(new Identifier("print"), IdentifierType.METHOD);
        reservedIdentifiers.put(new Identifier("int"), IdentifierType.TYPE_DECLARATION);
        reservedIdentifiers.put(new Identifier("string"), IdentifierType.TYPE_DECLARATION);
        reservedIdentifiers.put(new Identifier("bool"), IdentifierType.TYPE_DECLARATION);
        reservedIdentifiers.put(new Identifier("if"), IdentifierType.IF);
        reservedIdentifiers.put(new Identifier("for"), IdentifierType.FOR);
        reservedIdentifiers.put(new Identifier("while"), IdentifierType.WHILE);
        reservedIdentifiers.put(new Identifier("true"), IdentifierType.BOOLEAN);
        reservedIdentifiers.put(new Identifier("false"), IdentifierType.BOOLEAN);
        reservedIdentifiers.put(new Identifier("elif"), IdentifierType.ELIF);
        reservedIdentifiers.put(new Identifier("else"), IdentifierType.ELSE);

    }

    public void run() throws Exception {
        while(!lexicalAnalyzer.isEmpty()){
            lexicalAnalyzer.lex();
            statements();
        }
    }

    private void statements() throws Exception {
        CharClass tokenType = lexicalAnalyzer.getTokenType();

        if(tokenType != CharClass.IDENTIFIER){
            throw new Exception("Invalid statement on line " + lexicalAnalyzer.getLineIndex());
        }
        String expectedEnder = ";";

        Identifier identifier = identifier();
        IdentifierType identifierType = reservedIdentifiers.get(identifier);
        if(identifierType == IdentifierType.METHOD){
            methodCall(identifier);
        }else if(identifierType == IdentifierType.TYPE_DECLARATION){
            declareVar(identifier);
        }else if(identifierType == IdentifierType.VAR){
            assignVar(identifier);
        }else if(identifierType == IdentifierType.IF){
            ifStatement();
            expectedEnder = "}";
        }
        else{
            throw new Exception("Unknown identifier on line " + lexicalAnalyzer.getLineIndex());
        }

        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals(expectedEnder)){
            throw new Exception("Missing semicolon on line " + lexicalAnalyzer.getLineIndex());
        }
    }

    private String str() throws Exception{
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

    private Object expression() throws Exception{
        Stack<String> operatorStack = new Stack<>();
        Stack<Object> outputStack = new Stack<>();
        int parenthesesInStack = 0;

        //Shunting yard algorithm
        while(true){
            String lexeme = lexicalAnalyzer.getLexeme().strip();
            CharClass tokenType = lexicalAnalyzer.getTokenType();
            if(tokenType.isTerm()){
                outputStack.push(term());
                continue;
            }

            if(lexeme.equals("(")){
                parenthesesInStack++;
                operatorStack.push(lexeme);
                lexicalAnalyzer.lex();
                continue;
            }

            if(tokenType.isOperator()){
                int precedence = OperatorEvaluationMap.map.get(lexeme).precedence();
                while(!operatorStack.isEmpty() && !operatorStack.peek().equals("(") && precedence < OperatorEvaluationMap.map.get(operatorStack.peek()).precedence()) {
                    outputStack.push(OperatorEvaluationMap.map.get(operatorStack.pop()).evaluate(outputStack, lexicalAnalyzer.getLineIndex()));
                }

                lexicalAnalyzer.lex();
                operatorStack.push(lexeme);
                continue;
            }

            if(lexeme.equals(")")){

                if(parenthesesInStack == 0){
                    break;
                }
                if(operatorStack.peek().equals("(")){
                    throw new Exception("Empty sub expression on line " + lexicalAnalyzer.getLineIndex());
                }
                while(!operatorStack.peek().equals("(")){
                    outputStack.push(OperatorEvaluationMap.map.get(operatorStack.pop()).evaluate(outputStack, lexicalAnalyzer.getLineIndex()));
                }
                parenthesesInStack--;
                operatorStack.pop();
                lexicalAnalyzer.lex();
                continue;
            }
            break;
        }

        while(!operatorStack.isEmpty()){
            String operator = operatorStack.pop();
            if(operator.equals("(")){
                throw new Exception("Improper expression on line " + lexicalAnalyzer.getLineIndex());
            }
            outputStack.push(OperatorEvaluationMap.map.get(operator).evaluate(outputStack, lexicalAnalyzer.getLineIndex()));
        }

        if(outputStack.size() != 1){
            throw new Exception("Improper expression on line " + lexicalAnalyzer.getLineIndex());
        }

        return outputStack.pop();
    }

    private Object term() throws Exception{
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        Object resolvedTerm;

        if(tokenType == CharClass.DOUBLE_QUOTE){
            resolvedTerm = str();
        }else if(tokenType == CharClass.IDENTIFIER){
            Identifier identifier = identifier();
            if(!reservedIdentifiers.containsKey(identifier)){
                throw new Exception("Unknown identifier on line " + lexicalAnalyzer.getLineIndex());
            }
            IdentifierType identifierType = reservedIdentifiers.get(identifier);
            if(identifierType == IdentifierType.VAR){
                resolvedTerm = varMap.get(identifier);
            }else if(identifierType == IdentifierType.BOOLEAN){
                resolvedTerm = bool(identifier);
            }else if(identifierType == IdentifierType.METHOD){
                throw new Exception("Methods as expression arguments not yet implemented on line " + lexicalAnalyzer.getLineIndex());
            }else{
                throw new Exception("Invalid identifier in expression on line " + lexicalAnalyzer.getLineIndex());
            }

        }else if(tokenType == CharClass.DIGIT){
            resolvedTerm = intLiteral();
        }else{
            throw new Exception("Attempted to parse unimplemented expression on line " + lexicalAnalyzer.getLineIndex());
        }
        return resolvedTerm;
    }

    private Integer intLiteral() throws Exception{
        CharClass tokenType = lexicalAnalyzer.getTokenType();
        String lexeme = lexicalAnalyzer.getLexeme().strip();

        if(tokenType != CharClass.DIGIT){
            throw new Exception("Fake integer on line " + lexicalAnalyzer.getLineIndex());
        }

        lexicalAnalyzer.lex();

        return Integer.parseInt(lexeme);
    }

    private Identifier identifier() throws Exception{
        Identifier identifier = new Identifier(lexicalAnalyzer.getLexeme().strip());
        lexicalAnalyzer.lex();

        return identifier;
    }

    private Boolean bool(Identifier identifier) throws Exception{
        if(identifier.equals("true")){
            return true;
        }else if(identifier.equals("false")){
            return false;
        }
        throw new Exception("Tried to initialize invalid boolean on line " + lexicalAnalyzer.getLineIndex());
    }

    private void methodCall(Identifier identifier) throws Exception{ //TODO: Generalize
        String lexeme = lexicalAnalyzer.getLexeme().strip();

        if(!lexeme.equals("(")){
            throw new Exception("Invalid method call on line " + lexicalAnalyzer.getLineIndex());
        }

        lexicalAnalyzer.lex();

        Object o = expression();

        lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals(")")){
            throw new Exception("Invalid method call on line " + lexicalAnalyzer.getLineIndex());
        }
        lexicalAnalyzer.lex();
        System.out.println(o.toString());
    }

    private void declareVar(Identifier type) throws Exception {
        if(lexicalAnalyzer.getTokenType() != CharClass.IDENTIFIER){
            throw new Exception("Improper variable initialization on line " + lexicalAnalyzer.getLineIndex());
        }
        Identifier name = identifier();

        if(reservedIdentifiers.keyReserved(name)){
            throw new Exception("Tried to create variable from reserved identifier on line " + lexicalAnalyzer.getLineIndex());
        }

        if(!lexicalAnalyzer.getLexeme().strip().equals("=")){
            throw new Exception("Must initialize variable on line " + lexicalAnalyzer.getLineIndex());
        }

        lexicalAnalyzer.lex();

        Object var = expression();
        if(type.equals("string") && !(var instanceof String)){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLineIndex());
        }else if(type.equals("int") && !(var instanceof Integer)){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLineIndex());
        }else if(type.equals("bool") && !(var instanceof Boolean)){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLineIndex());
        }
        varMap.put(name, var);
        reservedIdentifiers.put(name, IdentifierType.VAR);
    }

    private void assignVar(Identifier name) throws Exception{
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals("=")){
            throw new Exception("Improper variable assignment on line " + lexicalAnalyzer.getLineIndex());
        }

        lexicalAnalyzer.lex();
        Object var = expression();
        if(!var.getClass().equals(varMap.get(name).getClass())){
            throw new Exception("Mismatched types on line " + lexicalAnalyzer.getLineIndex());
        }

        varMap.put(name, var);

    }

    private void ifStatement() throws Exception{
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals("(")){
            throw new Exception("Invalid if condition on line " + lexicalAnalyzer.getLineIndex());
        }
        lexicalAnalyzer.lex();
        Object resolvedExpression = expression();
        if(!(resolvedExpression instanceof Boolean)){
            throw new Exception("Non boolean expression in if condition on line " + lexicalAnalyzer.getLineIndex());
        }

        lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals(")")){
            throw new Exception("Invalid if condition on line " + lexicalAnalyzer.getLineIndex());
        }
        lexicalAnalyzer.lex();

        lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals("{")){
            throw new Exception("Invalid if body on line " + lexicalAnalyzer.getLineIndex());
        }

        Boolean condition = (Boolean) resolvedExpression;
        if(condition){
            scope(true);
        }else{
            skipChunk();
        }
    }

    private void skipChunk() throws Exception {
        CharClass tokenType = lexicalAnalyzer.getTokenType();

        if(tokenType != CharClass.OPENER){
            throw new Exception("Improper use of skipChunk on line " + lexicalAnalyzer.getLineIndex());
        }

        Stack<Character> closers = new Stack<>();

        while(true){
            if(lexicalAnalyzer.getTokenType() == CharClass.DOUBLE_QUOTE){
                str();
                continue;
            }else if(lexicalAnalyzer.getTokenType() == CharClass.OPENER){
                closers.push(getCloser(lexicalAnalyzer.getLexeme().charAt(0)));
            }else if(lexicalAnalyzer.getTokenType() == CharClass.CLOSER){
                if(lexicalAnalyzer.getLexeme().strip().charAt(0) != closers.peek()){
                    throw new Exception("Improper expression on line " + lexicalAnalyzer.getLineIndex());
                }
                closers.pop();
                if(closers.isEmpty()){
                    break;
                }
            }
            lexicalAnalyzer.lex();
        };
    }

    private char getCloser(char c) throws Exception{
        String openers = "([{";
        String closers = ")]}";
        int charIndex = openers.indexOf(c);
        if(charIndex != -1){
            return closers.charAt(charIndex);
        }
        throw new Exception("Not an opener");
    }

    private void scope(boolean increaseScope) throws Exception{
        if(increaseScope){
            varMap.increaseScope();
            reservedIdentifiers.increaseScope();
        }
        String lexeme = lexicalAnalyzer.getLexeme().strip();
        if(!lexeme.equals("{")){
            throw new Exception("Improper scope on line " + lexicalAnalyzer.getLineIndex());
        }
        lexicalAnalyzer.lex();
        while(!lexicalAnalyzer.getLexeme().strip().equals("}")){
            statements();
            lexicalAnalyzer.lex();
        }

        if(increaseScope){
            varMap.decreaseScope();
            reservedIdentifiers.decreaseScope();
        }
    }
}
