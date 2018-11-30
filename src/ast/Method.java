package ast;
import java.util.*;

public class Method extends Member {
    public Method(String name, VariableList paramList, Type returnType, ArrayList<Statement> statementList, Qualifier qualifier) {
        this.name = name;
        this.paramList = paramList;
        this.returnType = returnType;
        this.statementList = statementList;
        this.qualifier = qualifier;
    }

    public Qualifier getQualifier(){
        return qualifier;
    }

    public String getName(){
        return name;
    }

    public VariableList getParamList(){
        return paramList;
    }

    public Type getReturnType(){
        return returnType;
    }

    public ArrayList<Statement> getStatementList(){
        return statementList;
    }

    private Qualifier qualifier;
    private String name;
    private VariableList paramList;
    private Type returnType;
    private ArrayList<Statement> statementList;
}
