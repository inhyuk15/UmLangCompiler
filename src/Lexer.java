import java.io.StringReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
public class Lexer {
    private static final String overallTokenPat =
            "(#.*)|" +            // 주석
            "(어+)|" +            // 변수접근자
            "(엄식\\?$)|" +        // 입력 연산자
            "(엄)|" +             // 대입 연산자
            "(식)|" +             // 출력 선언 연산자
            "([ \\t])|" +        // 곱셈 연산자
            "(\\.)|" +           // 증가 연산자
            "(,)|" +             // 감소 연산자
            "(ㅋ)|" +            // 문자 출력 연산자
            "(!)";              // 정수 출력 연산자

    public static enum Category {
        EOF(null), PLUSONE("."),MINUSONE(","),
        INPUT("엄식?"), VARIDX("어"),
        OUTPUTSTART("식"), OUTPUTINT("!"), OUTPUTCHAR("ㅋ"),
        MULTIPLEX(" "),
        ASSIGNMENT("엄"),
        ERROR(null);

        final private String lexeme;
        Category (String s) {
            lexeme = s;
        }
    }
    public String lastLexeme;
    private Scanner inp;
    private  HashMap<String, Category> tokenMap =
            new HashMap<>();
    Lexer () {
        for (Category c : Category.values()) tokenMap.put(c.lexeme, c);
    }
    public void setInput(String line) {
        inp = new Scanner(new StringReader(line));
    }
    public Category nextToken() {
        // 주석 처리
        if (inp.findWithinHorizon(overallTokenPat, 0) == null) {
            return Category.EOF;
        }
        else {
            lastLexeme = inp.match().group(0);
            if (inp.match().start(1) != -1){
                return nextToken();
            }
            else if (inp.match().start(2) != -1)
                return Category.VARIDX;
            else if (inp.match().start(3) != -1)
                return Category.INPUT;
            else if (inp.match().start(4) != -1)
                return Category.ASSIGNMENT;
            else if (inp.match().start(5) != -1)
                return Category.OUTPUTSTART;
            else if (inp.match().start(6) != -1)
                return Category.MULTIPLEX;
            else if (inp.match().start(7) != -1)
                return Category.PLUSONE;
            else if (inp.match().start(8) != -1)
                return Category.MINUSONE;
            else if (inp.match().start(9) != -1)
                return Category.OUTPUTCHAR;
            else if (inp.match().start(10) != -1)
                return Category.OUTPUTINT;

            System.out.println("mmmm...." + lastLexeme);
            Category result = tokenMap.get(lastLexeme);
            if (result == null)
                return Category.ERROR;
            else
                return result;
        }
    }
}
