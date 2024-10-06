import java.util.EnumSet;

public enum CharClass {
    UNKNOWN{
        public boolean isType(char c){ return false; }
    },
    IDENTIFIER {
        public boolean isType(char c){ return Character.isDigit(c) || Character.isLetter(c) || c == '_'; }
    },
    DIGIT{
        public boolean isType(char c){ return Character.isDigit(c); }
    },
    WHITESPACE{
        public boolean isType(char c){ return Character.isWhitespace(c); }
    },
    DOUBLE_QUOTE{ public boolean isType(char c) { return c == '"'; }};

    public static final EnumSet<CharClass> singles = EnumSet.of(UNKNOWN,DOUBLE_QUOTE);

    abstract public boolean isType(char c);

    public static CharClass firstCharType(char c){
        if(Character.isLetter(c)){
            return IDENTIFIER;
        }
        if(Character.isDigit(c)){
            return DIGIT;
        }
        if(Character.isWhitespace(c)){
            return WHITESPACE;
        }
        if(c == '"'){
            return DOUBLE_QUOTE;
        }
        return UNKNOWN;
    }

    public boolean isSingle(){
        return singles.contains(this);
    }
}
