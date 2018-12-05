package ast;

public class AssertStat extends Statement {
    public AssertStat(String s){
        this.s = s;
    }

    private String s;
}
