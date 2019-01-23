/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;
import java.util.*;

public class LocalVariableList extends Statement {

    public LocalVariableList(){
        variableList = new ArrayList<>();
    }

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
