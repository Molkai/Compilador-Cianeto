package ast;

public class TypeNull extends Type {

	public TypeNull() {
		super("NullType");
	}

	public String getCname() {
		return "NULL";
	}

}
