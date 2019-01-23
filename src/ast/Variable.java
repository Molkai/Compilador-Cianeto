/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;
import java.util.*;

public class Variable {

    public Variable(String name, Type type){
        this.type = type;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Type getType(){
        return type;
    }

    private Type type;
    private String name;
}
