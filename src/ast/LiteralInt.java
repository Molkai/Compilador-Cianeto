/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class LiteralInt extends Expr {

    public LiteralInt( int value ) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    public void genC( PW pw, boolean putParenthesis ) {
        pw.printIdent("" + value);
    }

    public Type getType() {
        return Type.intType;
    }

    private int value;
}
