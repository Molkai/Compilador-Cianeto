package ast;

public class WriteStat extends Statement {
    public WriteStat(Type t){
        this.t = t;
    }

    private Type t;
}
