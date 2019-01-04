package ast;

public class Qualifier {

    public Qualifier( boolean pub, boolean prv, boolean fin, boolean over ) {
        this.pub = pub;
        this.prv = prv;
        this.fin = fin;
        this.over = over;
    }

    public boolean isPublic() {
        return pub;
    }

    public boolean isPrivate() {
        return prv;
    }

    public boolean isOverride() {
        return over;
    }

    public boolean isFinal() {
        return fin;
    }

    private boolean pub, prv, fin, over;
}
