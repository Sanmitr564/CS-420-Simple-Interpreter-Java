public class Identifier{
    public String identifier;

    public Identifier(String identifier){
        this.identifier = identifier;
    }

    public String getIdentifier(){ return this.identifier; }

    @Override
    public String toString(){
        return this.identifier;
    }

    @Override
    public boolean equals(Object s){
        return this.identifier.equals(s.toString());
    }

    @Override
    public int hashCode(){
        return identifier.hashCode();
    }
}
