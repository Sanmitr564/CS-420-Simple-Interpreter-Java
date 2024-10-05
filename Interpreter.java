import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Interpreter {
    public static void main(String[] args) throws Exception {

        if(args.length < 1){
            throw new Exception("Please provide input file");
        }
        String fileName = args[0];

        ArrayList<String> program = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("src/" + fileName))) {
            while (br.ready()) {
                program.add(br.readLine());
            }
        }

        Parser parser = new Parser(program);
        parser.run();

    }
}
