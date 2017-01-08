import com.mola.charts.ChartType;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.weka.managers.WekaPersistenceManager;
import com.mola.weka.models.WekaArffModel;

import java.util.List;


public class WekaPersistanceManagerTest {
    WekaPersistenceManager manager = new WekaPersistenceManager();

    public static void main(String[] args) {
        new WekaPersistanceManagerTest().test();
    }

    private void test() {
        manager.initialize();
        ChartType[] types = new ChartType[3];
        types[0] = ChartType.ema;
        types[1] = ChartType.sma;
        types[2] = ChartType.stoch;

        List<WekaArffModel> results = (List<WekaArffModel>) manager.getChart(Pair.EUR_USD, Granularity.M1, null);
        System.out.println("manager.getChart().size: " + results.size());
    }

}
