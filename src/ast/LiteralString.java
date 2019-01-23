/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class LiteralString extends Expr {

    public LiteralString( String literalString ) {
        this.literalString = literalString;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        pw.print(literalString);
    }


    public Type getType() {
        return Type.stringType;
    }

    private String literalString;
}
