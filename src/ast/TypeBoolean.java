package ast;

public class TypeBoolean extends Type {

   public TypeBoolean() { super("boolean"); }

   public String getCname() {
      return "int";
   }

}
