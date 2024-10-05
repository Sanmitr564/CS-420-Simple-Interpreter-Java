import java.util.ArrayList;

public class LexicalAnalyzer {
    private final ArrayList<String> program;
    private char nextChar;
    private CharClass charClass;
    private CharClass tokenType;
    private String lexeme;
    private int line;


    public LexicalAnalyzer(ArrayList<String> program){
        this.program = program;
        line = 1;
    }

    public void lex() throws Exception{
        lexeme = "";
        getChar();

        while(charClass == CharClass.WHITESPACE){
            addChar();
            getChar();
        }
        tokenType = charClass;

        if(tokenType == CharClass.UNKNOWN){
            addChar();
            return;
        }

        do{
            addChar();
            getChar();
        }while(tokenType.isType(nextChar));

    }

    private void getChar() throws Exception {
        if(program.size() == 0){
            throw new Exception("Invalid statement on line " + line);
        }
        if(program.get(0).length() == 0){
            program.remove(0);
            line++;
            getChar();
        }

        String s = program.get(0);
        nextChar = s.charAt(0);
        charClass = CharClass.firstCharType(nextChar);
    }

    private void addChar(){
        lexeme += nextChar;
        String s = program.get(0);
        if(s.length() == 1){
            program.remove(0);
            line++;
        }else{
            program.set(0, s.substring(1));
        }
    }

    public String getLexeme(){
        return lexeme;
    }

    public CharClass getTokenType(){
        return tokenType;
    }

    public int getLine(){ return line; }

    public boolean isEmpty(){
        return program.isEmpty();
    }
}
