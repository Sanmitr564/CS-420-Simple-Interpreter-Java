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

    public static final EnumSet<IdentifierType> ifTypes = EnumSet.of(IF, ELIF, ELSE);

    public static final EnumSet<IdentifierType> ifSuccessors = EnumSet.of(ELIF, ELSE);

    public boolean isIfType(){
        return ifTypes.contains(this);
    }

    public boolean isIfSuccessor(){
        return ifSuccessors.contains(this);
    }
}
