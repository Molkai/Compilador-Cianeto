/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class TypeString extends Type {

    public TypeString() {
        super("String");
    }

   public String getCname() {
      return "char *";
   }

}
