package ast;
import java.util.*;
/*
 * Krakatoa Class
 */
public class CianetoClass extends Type {

    public CianetoClass( String name, boolean open, CianetoClass superClass, ArrayList<Member> memberList ) {
        super(name);
        this.open = open;
        this.superClass = superClass;
        this.memberList = memberList;
    }

    public String getCname() {
        return getName();
    }

    public CianetoClass getSClass() {
        return superClass;
    }

    public boolean getOpen(){
        return open;
    }

    public ArrayList<Member> getMemberList(){
        return memberList;
    }

    private String name;
    private CianetoClass superClass;
    private ArrayList<Member> memberList;
    private boolean open;
    // metodos publicos get e set para obter e iniciar as variaveis acima,
    // entre outros metodos
}
