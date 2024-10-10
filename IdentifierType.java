import java.util.EnumSet;

public enum IdentifierType {
    TYPE_DECLARATION,
    VAR,
    METHOD,
    IF,
    ELIF,
    ELSE,
    FOR,
    WHILE,
    BOOLEAN;

    public static final EnumSet<IdentifierType> statementStarter = EnumSet.of(TYPE_DECLARATION, VAR, METHOD);

    public static final EnumSet<IdentifierType> conditionalStarter = EnumSet.of(IF, FOR, WHILE);

    public boolean isStatement(){
        return statementStarter.contains(this);
    }

    public boolean isConditional(){
        return conditionalStarter.contains(this);
    }
}
