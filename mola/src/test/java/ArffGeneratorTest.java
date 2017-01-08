import com.mola.charts.util.ArffGenerator;

import java.io.IOException;

public class ArffGeneratorTest {

    public static void main(String[] args) {
        ArffGenerator generator = new ArffGenerator();
        generator.setInputDirectory("E:\\dev\\thinktank\\Trading System\\stockdata");
        try {
            generator.generate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
