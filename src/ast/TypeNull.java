/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;

public class TypeNull extends Type {

	public TypeNull() {
		super("NullType");
	}

	public String getCname() {
		return "NULL";
	}

}
