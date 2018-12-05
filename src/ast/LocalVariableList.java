package ast;
import java.util.*;

public class LocalVariableList extends Statement {

    public void add(Variable v){
        variableList.add(v);
    }

    public int getSize(){
        return variableList.size();
    }

    public Variable get(int i){
        return variableList.get(i);
    }

    private ArrayList<Variable> variableList;
}
