/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class WriteStat extends Statement {
    public WriteStat(Type t){
        this.t = t;
    }

    private Type t;
}
