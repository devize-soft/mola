import com.mola.charts.BaseChart;
import com.mola.charts.Granularity;
import com.mola.charts.Pair;
import com.mola.charts.averages.EMAChart;
import com.mola.charts.averages.SMAChart;
import com.mola.charts.oscillators.StochasticOscillator;
import com.mola.charts.util.ChartFetcher;
import com.mola.oanda.OandaCandleStickChart;
import com.mola.weka.managers.WekaPersistenceManager;
import com.mola.weka.managers.WekaTimeSeriesManager;
import com.mola.weka.models.WekaArffModel;

import java.io.*;
import java.util.*;

public class JavaJsonTest {

    private List<String> keyData = new ArrayList<String>();
    private Map<String, BaseChart> chartsMap = new HashMap<String, BaseChart>();
    private static WekaPersistenceManager manager = new WekaPersistenceManager().initialize();

    public static void main(String[] args) throws IOException {
        new JavaJsonTest().process();
    }

    public void process() {

        Calendar end = Calendar.getInstance();
//		end.setTime(new Date(System.currentTimeMillis()));
//		end.set(Calendar.MONTH, 11);
//		end.set(Calendar.DAY_OF_MONTH, 22);
//		end.set(Calendar.YEAR, 2014);

        for (Pair pair : Pair.values()) {
            for (Granularity g : Granularity.values()) {
                BaseChart c = new OandaCandleStickChart();
                c.setPair(pair);
                c.setGranularity(g);
                Date sDate = getGranularityBasedStart(g, end.getTime());
                ChartFetcher cf = new ChartFetcher(pair, g);
                cf.fetchChart(null, "", c, sDate, end.getTime(), 14);
                c.setStartDate(sDate);
                c.setEndDate(end.getTime());
                chartsMap.put(c.getClass().getName() + "_" + pair.name() + "." + g.name(), c);
            }
        }

//		BaseChart chart = new OandaCandleStickChart();
//		chart.setPair(Pairs.EUR_USD);
//		chart.setGranularity(Granularity.D);
//		// TODO this CAL is for testing only, ideally date values should be set
//		// dynamically by the user
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(new Date(System.currentTimeMillis()));
//		cal.set(Calendar.MONTH, 11);
//		cal.set(Calendar.DAY_OF_MONTH, 21);
//		cal.set(Calendar.YEAR, 2014);

        fetchCharts();
        System.out.println("");
    }

    File file = new File("C:\\testarff.arff");
    FileOutputStream fos;

    private void fetchCharts() {

        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, BaseChart> entry : chartsMap.entrySet()) {
            keyData.clear();
            BaseChart chart = entry.getValue();
            // ChartFetcher fetcher = new ChartFetcher();
            //
            // fetcher.fetchChart(chart, cal.getTime(), end.getTime(), 14);
            chart.setOffset(100);
            // chart.saveChart();
            chart.addChart(new StochasticOscillator(chart, 14, 3, 3));
            chart.addChart(new SMAChart(chart, 30));
            chart.addChart(new EMAChart(chart, 30));
            chart.render();
            chart.renderAuxillaryCharts();

            List<BaseChart> auxillaryCharts = chart.getAllCharts();

            int auxChartCount = chart.countRenderedData();
            int maxRows = chart.getMaxRows();
            Object[][] grid = new Object[auxChartCount][maxRows];

            if (auxillaryCharts.size() > 0) {
                int currentColumn = 0;
                int tempColumn = 0;
                int resetColumn = 0;

                for (int i = 0; i < auxillaryCharts.size(); ++i) {
                    BaseChart auxChart = auxillaryCharts.get(i);
                    Map<String, Object[]> data = auxChart.getRenderedData();
                    int startIndex = chart.getLength() - chart.getMaxRows();
                    // get key strings
                    List<String> keys = getKeys(auxChart);
                    List<Object[]> tempKeyData = new ArrayList<Object[]>();
                    for (String key : keys) {
                        tempKeyData.add(data.get(key));
                    }
                    tempColumn = currentColumn;
                    for (int j = 0; j < maxRows; ++j) {
                        for (int k = 0; k < tempKeyData.size(); ++k) {
                            grid[tempColumn][j] = tempKeyData.get(k)[startIndex];
                            if (tempColumn < keyData.size()) {
                                tempColumn++;
                            }
                            if (k >= keys.size() - 1) {
                                currentColumn = tempColumn;
                                tempColumn = resetColumn;
                            }
                        }
                        ++startIndex;
                    }
                    resetColumn = currentColumn;
                }
            }
            buildArff(keyData, grid, chart);
        }

    }

    private Date getGranularityBasedStart(Granularity g, Date end) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        /*switch (g) {
		case D:
			cal.add(Calendar.MONTH, -1);
			break;
		case H1:
			cal.add(Calendar.DAY_OF_YEAR, -5);
			break;
		case H2:
			cal.add(Calendar.DAY_OF_YEAR, -10);
			break;
		case M30:
			cal.add(Calendar.DAY_OF_YEAR, -3);
			break;
		}*/
        return cal.getTime();
    }

    public static String buildArff(List<String> keys, Object[][] grid, BaseChart chart) {

        StringBuilder builder = new StringBuilder();
        builder.append("%URL: " + chart.getUrl().toString() + "\r\n\r\n");
        builder.append("@relation " + chart.getPair().name() + "." + System.currentTimeMillis());
        builder.append("\r\n");
        builder.append("\r\n");

        for (int i = 0; i < keys.size(); ++i) {
            String type = "numeric";
            String[] attr = keys.get(i).split(":");
            if (attr.length > 1) {
                type = attr[1];
            }
            builder.append("@attribute " + keys.get(i) + " " + type);
            if ("date".equalsIgnoreCase(type)) {
                builder.append("  \"dd.MM.yyyy HH:mm:ss.SSS\"");
            }
            builder.append("\r\n");
        }

        builder.append("\r\n");
        builder.append("@data");
        builder.append("\r\n");

        for (int i = (chart.getLength() - chart.getMaxRows()); i < chart.getMaxRows(); ++i) {
            for (int j = 0; j < grid.length; ++j) {
                builder.append(grid[j][i]);
                if (j != (grid.length - 1)) {
                    builder.append(",");
                } else {
                    builder.append("\r\n");
                }
            }
        }

        System.out.println(builder.toString());
        // TODO remove print write stuff
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("c:\\testout.arff", true)));
            out.append(builder.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WekaTimeSeriesManager timeSeriesManager = new WekaTimeSeriesManager();
        timeSeriesManager.process(new ByteArrayInputStream(builder.toString().getBytes()));
        WekaArffModel model = new WekaArffModel(chart);
        model.setArff(builder.toString());
        manager.persistModel(model);
        return builder.toString();
    }

    public void createMappedFile() {
        // int BUFFER_SIZE = 4 * 1024 * 1024; // 4MB
        // BUFFER_SIZE = builder.toString().length();
        // String fileName = "C:\\temparff" + System.currentTimeMillis() +
        // ".arff";
        // File f = new File(fileName);
        // f.delete();
        // RandomAccessFile randomAccessFile;
        // MappedByteBuffer mem;
        // try {
        // randomAccessFile = new RandomAccessFile(f, "rw");
        // FileChannel fc = randomAccessFile.getChannel();
        // mem = fc.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_SIZE);
        // int start = 0;
        // long counter = 1;
        // long HUNDREDK = 100000;
        // long noOfMessage = HUNDREDK * 10 * 10;
        // for (;;) {
        // if (!mem.hasRemaining()) {
        // start += mem.position();
        // mem = fc.map(FileChannel.MapMode.READ_WRITE, start, BUFFER_SIZE);
        // }
        // mem.putLong(counter);
        // counter++;
        // if (counter > noOfMessage)
        // break;
        // }
        // randomAccessFile.close();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }

    public List<String> getKeys(BaseChart baseChart) {
        Map<String, Object[]> data = baseChart.getRenderedData();
        List<String> keys = new ArrayList<String>();
        for (Map.Entry<String, Object[]> entry : data.entrySet()) {
            if (!"arff".equals(entry.getKey())) {
                keys.add(entry.getKey());
            }
            for (String k : keys) {
                System.out.println(k);
            }
        }
        keyData.addAll(keys);
        return keys;
    }
}
