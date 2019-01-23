/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class TypeUndefined extends Type {
    // variables that are not declared have this type

   public TypeUndefined() { super("undefined"); }

   public String getCname() {
      return "int";
   }

}
