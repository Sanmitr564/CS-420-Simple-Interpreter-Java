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
    WHITESPACE{ public boolean isType(char c){ return Character.isWhitespace(c); }},
    DOUBLE_QUOTE{ public boolean isType(char c) { return c == '"'; }},
    MATH_OPERATOR{ public boolean isType(char c) {
        return c == '+' || c == '-' || c == '/' || c == '*' || c == '%';
    }},
    BOOL_OPERATOR{ public boolean isType(char c){
        return c == '=' || c == '&' || c == '|' || c == '!';
    }},
    OPENER{
        public boolean isType(char c) { return c == '(' || c == '{' || c == '['; }
    },
    CLOSER{
        public boolean isType(char c) {return c == ')' || c == '}' || c == ']'; }
    };

    public static final EnumSet<CharClass> singles = EnumSet.of(UNKNOWN, DOUBLE_QUOTE, MATH_OPERATOR, OPENER, CLOSER);

    public static final EnumSet<CharClass> operators = EnumSet.of(MATH_OPERATOR, BOOL_OPERATOR);

    public static final EnumSet<CharClass> terms = EnumSet.of(DIGIT, DOUBLE_QUOTE, IDENTIFIER);

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
        if(DOUBLE_QUOTE.isType(c)){
            return DOUBLE_QUOTE;
        }
        if(MATH_OPERATOR.isType(c)){
            return MATH_OPERATOR;
        }
        if(BOOL_OPERATOR.isType(c)){
            return BOOL_OPERATOR;
        }
        if(OPENER.isType(c)){
            return OPENER;
        }
        if(CLOSER.isType(c)){
            return CLOSER;
        }

        return UNKNOWN;
    }

    public boolean isSingle(){
        return singles.contains(this);
    }

    public boolean isOperator() { return operators.contains(this); }

    public boolean isTerm() { return terms.contains(this); }
}
