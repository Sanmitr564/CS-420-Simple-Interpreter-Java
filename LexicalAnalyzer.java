import java.util.ArrayList;

public class LexicalAnalyzer {
    private final ArrayList<String> program;
    private char nextChar;
    private CharClass charClass;
    private CharClass tokenType;
    private String lexeme;
    private int lineIndex;
    private int charIndex;


    public LexicalAnalyzer(ArrayList<String> program){
        this.program = program;
        lineIndex = 0;
        charIndex = 0;
    }

    public void lex() throws Exception{
        lexeme = "";
        getChar();

        while(charClass == CharClass.WHITESPACE){
            addChar();
            getChar();
        }
        tokenType = charClass;

        if(tokenType.isSingle()){
            addChar();
            return;
        }

        do{
            addChar();
            getChar();
        }while(tokenType.isType(nextChar));

    }

    private void getChar() throws Exception {
        if(program.isEmpty()){
            throw new Exception("Invalid statement on line " + lineIndex);
        }
        if(program.get(lineIndex).length() == charIndex){
            lineIndex++;
            charIndex = 0;
            getChar();
        }

        String s = program.get(lineIndex);
        nextChar = s.charAt(charIndex);
        charClass = CharClass.firstCharType(nextChar);
    }

    private void addChar(){
        lexeme += nextChar;
        String s = program.get(lineIndex);
        if(s.length() == charIndex){
            lineIndex++;
            charIndex = 0;
        }else{
            charIndex++;
        }
    }

    public String getLexeme(){
        return lexeme;
    }

    public CharClass getTokenType(){
        return tokenType;
    }

    public int getLineIndex(){ return lineIndex; }

    public boolean isEmpty(){
        return lineIndex == program.size() - 1 && charIndex == program.get(program.size() - 1).length();
    }

    public void goTo(int line, int charIndex){
        this.lineIndex = line;
        this.charIndex = charIndex;
    }
}
