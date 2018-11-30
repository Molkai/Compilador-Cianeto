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

    @Override
    public String getCname() {
        return getName();
    }

    public CianetoClass getSClass() {
        return superClass;
    }

    public boolean getOpen(){
        return open;
    }

    public Member getMemberList(){
        return memberList;
    }

    private String name;
    private CianetoClass superClass;
    private ArrayList<Member> memberList;
    private boolean open;
    // métodos públicos get e set para obter e iniciar as variáveis acima,
    // entre outros métodos
}
