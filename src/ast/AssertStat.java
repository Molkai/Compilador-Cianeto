/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class AssertStat extends Statement {
    public AssertStat(String s){
        this.s = s;
    }

    private String s;
}
