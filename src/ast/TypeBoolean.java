/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class TypeBoolean extends Type {

   public TypeBoolean() { super("boolean"); }

   public String getCname() {
      return "int";
   }

}
