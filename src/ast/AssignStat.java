package ast;

public class AssignStat extends Statement {
    public AssignStat(Type t1, Type t2){
        this.t1 = t1;
        this.t2 = t2;
    }

    private Type t1;
    private Type t2;
}
