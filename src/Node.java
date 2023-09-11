import java.util.ArrayList;
import java.util.List;

public class Node {
    // var1, var2는 T 그 외는 NT
    public enum NodeType {
        VAR1,            // "어*"
        VAR2,            // <var1> | <var1> <incDec>
        PLUSONE,         // .+
        MINUSONE,        // ,+
        INCDEC,          // <plusOne> | <minusOne>
        MULTIPLEX,       // <var2> " " <var2>
        INTEGER,         // <multiplex> | <var2>
        ASSIGNMENT,      // <var1> "엄" <integer>
        OUTPUTCHAR,      // "식" <integer> "ㅋ"
        OUTPUTINT,       // "식" <integer> "!"
        INPUT            // <var1> "엄식?"
    }

    private NodeType type;
    private String value;
    private List<Node> children;

    public Node(NodeType type, String value) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public NodeType getType() {
        return type;
    }

    public String getValue() {
        switch (type) {
            case ASSIGNMENT:
                String lh = children.get(0).getValue(); // var1
                String rh = children.get(1).getValue(); // integer
                return lh + "=" + rh;
            case OUTPUTCHAR:
                String val1 = children.get(0).getValue(); // integer
                return "printf(\"%c\"," + val1 + ");";
            case OUTPUTINT:
                String val2 = children.get(0).getValue(); // integer
                return "printf(\"%d\"," + val2 + ");";
            case INPUT:
                String val3 = children.get(0).getValue(); // var1
                return "scanf(\"%d\", &" +  val3 + ");";
            case INTEGER:
                String lh1 = children.get(0).getValue(); // var2
                if (children.size() > 1) {
                    String rh1 = children.get(1).getValue(); // multiplex
                    return lh1 + rh1;
                }
                return children.get(0).getValue(); // multiplex or var2
            case MULTIPLEX:
                String rh2 = children.get(0).getValue(); // var2
                return "*" + rh2;
            case VAR2:
                String lh3 = children.get(0).getValue();
                if (children.size() > 1) {
                    String rh3 = children.get(1).getValue();
                    return lh3 + rh3;
                }
                return lh3;
            case VAR1:
                return value;
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public String getRealValue() { return this.value; }
    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }

}
