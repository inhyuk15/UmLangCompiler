import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static String compiledContent;
    public static void main(String[] args) {
        String inputFilePath = "test1.umm";
        String outputFilePath = "test1.c";
        Lexer lexer = new Lexer();
        // intro
        compiledContent = "";
        compiledContent += "#include <stdio.h>\n";
        compiledContent += "int main() {\n";
        compiledContent += "int var[100];\n";

        // read
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(("어떻게")) || line.equals(("이 사람이름이냐ᄏᄏ"))) continue;
                lexer.setInput(line);
                List<Lexer.Category> tokens = new ArrayList<>();
                Lexer.Category token;
                do {
                    token = lexer.nextToken();
//                    System.out.println(token);
                    tokens.add(token);
                } while (token != Lexer.Category.EOF);
                Parser parser = new Parser(tokens);
                compiledContent += parser.getCLangCode();
                compiledContent += ";\n";
//                System.out.println("---------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // outro
        compiledContent += "return 0;\n";
        compiledContent += "}\n";

        // write
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath))) {
            bw.write(compiledContent);
            System.out.println("File written successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}