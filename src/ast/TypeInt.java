/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class TypeInt extends Type {

    public TypeInt() {
        super("int");
    }

   public String getCname() {
      return "int";
   }

}
