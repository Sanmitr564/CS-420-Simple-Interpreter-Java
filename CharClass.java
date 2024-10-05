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
    };

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
        return UNKNOWN;
    }
}
