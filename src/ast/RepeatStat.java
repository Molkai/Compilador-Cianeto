/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

import java.util.*;

public class RepeatStat extends Statement {
    public void add(Statement s){
        statementList.add(s);
    }

    private ArrayList<Statement> statementList = new ArrayList<>();
}
