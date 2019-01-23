/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class NullExpr extends Expr {

   public void genC( PW pw, boolean putParenthesis ) {
      pw.printIdent("NULL");
   }

   public Type getType() {
      //# corrija
      return null;
   }
}
