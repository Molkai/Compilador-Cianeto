/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package ast;
import java.util.*;
/*
 * Krakatoa Class
 */
public class CianetoClass extends Type {

    public CianetoClass( String name, boolean open, CianetoClass superClass ) {
        super(name);
        this.open = open;
        this.superClass = superClass;
        this.memberList = new ArrayList<>();
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

    public void addMember(Member m){
        memberList.add(m);
    }

    private String name;
    private CianetoClass superClass;
    private ArrayList<Member> memberList;
    private boolean open;
    // metodos publicos get e set para obter e iniciar as variaveis acima,
    // entre outros metodos
}
