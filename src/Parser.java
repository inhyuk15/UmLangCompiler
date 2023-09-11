//      ****** Grammer ******
//    <program> ::= <assignment> | <outputChar> | <outputInt> | <input>
//    <assignment> ::= <var1> "엄" <integer>
//    <outputChar> ::= "식" <integer> "ㅋ"
//    <outputInt> ::= "식" <integer> "!"
//    <input> ::= <var1> "엄식?"
//    <integer> ::= <var2> | <multiplex>
//    <multiplex> ::= " " <integer>
//    <var2> ::= <var1> <incDec> | <var1> | <incDec>
//    <var1> ::= "어*"
//    <incDec> ::= <plusOne> | <minusOne>
//    <plusOne> ::= .+
//    <minusOne> ::= ,+
//      ****** Grammer ******

import java.util.List;
public class Parser {
    private final List<Lexer.Category> tokens;
    private Lexer.Category currentToken;
    private int idx = 0;
    public Parser(List<Lexer.Category> tokens) {
        this.tokens = tokens;
        if (!tokens.isEmpty()) {
            currentToken = tokens.get(0);
        } else {
            currentToken = Lexer.Category.EOF;
        }
    }

    public String getCLangCode() {
        Node rootNode = parseExpression();
        if (rootNode == null) return "";
        return rootNode.getValue();
    }

    // 파싱트리 생성
    public Node parseExpression() {
        Node node = null;

        node = parseOutputChar();
        if (node != null)
            return node;

        node = parseOutputInt();
        if (node != null) return node;

        node = parseInput();
        if (node != null) {
            return node;
        }

        node = parseAssignment();
        if (node != null) return node;

        return null;
    }

    public Node parseAssignment() {
        int initialIdx = idx; // 상태 저장
        Node var1 = parseVar1();
        if (var1 == null) var1 = new Node(Node.NodeType.VAR1, "var[1]");
        if (checkToken(Lexer.Category.ASSIGNMENT)) {
            consumeToken(); // 엄
            Node integer = parseInteger();
            if (integer != null) {
                Node assignmentNode = new Node(Node.NodeType.ASSIGNMENT, "ASSIGNMENT");
                assignmentNode.addChild(var1);
                assignmentNode.addChild(integer);
                return assignmentNode;
            }
        }
        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }
    public Node parseOutputChar() {
        int initialIdx = idx; // 상태 저장
        if (checkToken(Lexer.Category.OUTPUTSTART)) {
            consumeToken();
            Node outputCharNode = new Node(Node.NodeType.OUTPUTCHAR, "OUTPUTCHAR");
            Node integer = parseInteger();
            if (integer != null && checkToken(Lexer.Category.OUTPUTCHAR)) {
                consumeToken();
                outputCharNode.addChild(integer);
                return outputCharNode;
            }
        }
        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }

    public Node parseOutputInt() {
        int initialIdx = idx; // 상태 저장
        if (checkToken(Lexer.Category.OUTPUTSTART)) {
            consumeToken();
            Node outputIntNode = new Node(Node.NodeType.OUTPUTINT, "OUTPUTINT");
            Node integer = parseInteger();
            if (integer != null && checkToken(Lexer.Category.OUTPUTINT)) {
                consumeToken();
                outputIntNode.addChild(integer);
                return outputIntNode;
            }
        }
        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }
    public Node parseInput() {
        int initialIdx = idx; // 상태 저장
        Node var1 = parseVar1();
        if (var1 == null) var1 = new Node(Node.NodeType.VAR1, "1");
        if (checkToken(Lexer.Category.INPUT)) {
            consumeToken();  // '식?' 토큰 소비
            Node inputIntNode = new Node(Node.NodeType.INPUT, "INPUT");
            // 인덱스를 output이랑 좀 다르게 처리함
            inputIntNode.addChild(var1);
            return inputIntNode;
        }
        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }
    public Node parseInteger() {
        int initialIdx = idx; // 상태 저장

        Node var2 = parseVar2();
        if (var2 != null) {
            Node integerNode = new Node(Node.NodeType.INTEGER, "INTEGER");
            integerNode.addChild(var2);
            if (checkToken(Lexer.Category.MULTIPLEX)) {
                Node multiplex = parseMultiplex();
                if (multiplex != null) {
                    integerNode.addChild(multiplex);
                }
            }
            return integerNode;
        }

        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }
    public Node parseMultiplex() {
        int initialIdx = idx; // 상태 저장
        if (checkToken(Lexer.Category.MULTIPLEX)) {
            consumeToken();
            Node integer = parseInteger();
            if (integer != null) {
                Node multiplexNode = new Node(Node.NodeType.MULTIPLEX, "MULTIPLEX");
                multiplexNode.addChild(integer);
                return multiplexNode;
            }
        }
        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }
    public Node parseVar2() {
        int initialIdx = idx; // 상태 저장
        // <var1>
        Node var1 = parseVar1();
        if (var1 != null) {
            Node var2 = new Node(Node.NodeType.VAR2, "VAR2");
            var2.addChild(var1);
            // <var1><incDec>
            if (checkToken(Lexer.Category.PLUSONE) || checkToken(Lexer.Category.MINUSONE)) {
                Node incDec = parseIncDec();
                if (!incDec.getValue().equals("0")) {
                    var2.addChild(incDec);
                }
            }
            return var2;
        }
        // <incDec>
        if (checkToken(Lexer.Category.PLUSONE) || checkToken(Lexer.Category.MINUSONE)) {
            Node incDec = parseIncDec();
            Node var2 = new Node(Node.NodeType.VAR2, "VAR2");
            var2.addChild(incDec);
            return var2;
        }

        setIdx(initialIdx); // 백트레킹: 상태 복원
        return null;
    }
    public Node parseVar1() {
        Integer cnt = 0;
        while (checkToken(Lexer.Category.VARIDX)) {
            consumeToken();
            cnt++;
        }
        if (cnt != 0) {
            return new Node(Node.NodeType.VAR1, "var[" + cnt.toString() + "]");
        }
        return null;
    }
    public Node parseIncDec() {
        Integer number = 0;

        while (checkToken(Lexer.Category.PLUSONE) || checkToken(Lexer.Category.MINUSONE)) {
            if (checkToken(Lexer.Category.PLUSONE)) number++;
            else number--;
            consumeToken();
        }

        return new Node(Node.NodeType.INCDEC, number.toString());
    }

    private Lexer.Category nextToken() {
        if (idx + 1 < tokens.size()) {
            idx++;
            return tokens.get(idx);
        } else {
            return Lexer.Category.EOF;
        }
    }
    private String consumeToken() {
        nextToken();
        currentToken = currentToken();
        // 이거 맞나
        return currentToken.name();
    }
    private Lexer.Category currentToken() {
        return tokens.get(idx);
    }
    private boolean checkToken(Lexer.Category token) {
        return currentToken == token;
    }

    private void setIdx(int idx) {
        this.idx = idx;
        currentToken = tokens.get(idx);
    }
}
