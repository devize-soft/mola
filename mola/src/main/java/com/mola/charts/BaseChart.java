package com.mola.charts;

import com.mola.instruments.Quote;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class BaseChart implements AbstractAuxillaryChart {
    protected Logger logger = Logger.getLogger(BaseChart.class.getSimpleName());
    private Long id;
    private Map<Date, Quote<String, Object>> price = new TreeMap<Date, Quote<String, Object>>();
    private List<BaseChart> auxillaryCharts = new ArrayList<BaseChart>();

    private Map<String, Object[]> renderedData = new HashMap<String, Object[]>();
    private int offset;
    private int length;
    private Pair pair;
    private Granularity granularity;
    private ChartType chartType;
    private URL url;
    private Period period = Period.DAY;
    protected static ArrayList<String> entries = new ArrayList<String>();
    protected Object[] quotes;
    protected BaseChart baseChart;
    private Date endDate;
    private Date startDate;

    public BaseChart() {

    }

    public BaseChart(Object[] quotes) {
        this.quotes = quotes;
    }

    public Quote<String, Object> buildQuote(JSONObject jsonObject) {
        Quote<String, Object> q = new Quote<String, Object>();
        try {
            for (String entry : entries) {
                q.put(entry, (jsonObject.get(entry)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return q;
    }

    public void addChart(BaseChart chart) {
        chart.setParent(this);
        auxillaryCharts.add(chart);
    }

    // @Transient
    public Object[] getQuotes() {
        String[] ret = new String[quotes.length];
        for (int i = 0; i < quotes.length; i++) {
            ret[i] = (quotes[i]).toString();
        }
        return ret;
    }

    public JSONObject[] getQuotesJson() {
        return (JSONObject[]) quotes;
    }

    public void setQuotes(Object[] quotes) {
        this.quotes = quotes;
    }

    public void renderAuxillaryCharts() {
        for (BaseChart chart : auxillaryCharts) {
            chart.render();
        }
    }

    // @javax.persistence.OneToMany(cascade = CascadeType.ALL)
    // @javax.persistence.MapKey()
    // @ManyToOne(targetEntity = Quote.class)
    // @Transient
    public Map<Date, Quote<String, Object>> getPrice() {
        return price;
    }

    public void setPrice(Map<Date, Quote<String, Object>> price) {
        this.price = price;
    }

    public void setParent(BaseChart chart) {
        this.baseChart = chart;
    }

    // @Transient
    public Map<String, Object[]> getRenderedData() {
        return renderedData;
    }

    public void setRenderedData(Map<String, Object[]> renderedData) {
        this.renderedData = renderedData;
    }

    public void putRenderedData(String key, Object[] data) {
        renderedData.put(key, data);
    }

    // @OneToMany(targetEntity = BaseChart.class, fetch = FetchType.EAGER)
    public List<BaseChart> getAuxillaryCharts() {
        return auxillaryCharts;
    }

    // @Transient
    public List<BaseChart> getAllCharts() {
        List<BaseChart> charts = new ArrayList<BaseChart>();
        charts.addAll(auxillaryCharts);
        charts.add(this);
        return charts;
    }

    // @Column
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /*
     * Some charts require previous data, example SMA may average 30 previous
     * prices thus numbers 0-30 will be empty 31-n will have a value. Likewise
     * Stoch may have a lag of 8 values, where 0-8 are empty and 9-n are
     * populated etc. Use this method when generating ARFF to prevent large
     * fields of 0.0
     */
    // @Transient
    public int getMaxOffset() {
        int maxOffset = offset;
        for (BaseChart chart : auxillaryCharts) {
            if (chart.getOffset() > maxOffset) {
                maxOffset = chart.getOffset();
            }
        }
        return maxOffset;
    }

    // For synching charts use the chart with least rows as a max.
    // @Transient
    public int getMaxRows() {
        int maxRows = Integer.MAX_VALUE;
        if (auxillaryCharts.size() == 0) {
            return this.getLength();
        }
        for (BaseChart chart : auxillaryCharts) {
            if (maxRows > chart.getLength()) {
                maxRows = chart.getLength();
            }
        }
        return maxRows;
    }

    public int countRenderedData() {
        int count = this.getRenderedData().size();
        for (BaseChart chart : auxillaryCharts) {
            count += chart.getRenderedData().size();
            if (chart.getRenderedData().get("arff") != null) {
                count--;
            }
        }
        return count;
    }

    public String[] doubleArrayToStringArray(double[] in) {
        String[] out = new String[in.length];

        for (int i = 0; i < out.length; i++)
            out[i] = String.valueOf(in[i]);

        return out;
    }

    // @Column
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    // @Lob
    public Pair getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair = pair;
    }

    // @Lob
    public Granularity getGranularity() {
        return granularity;
    }

    public void setGranularity(Granularity granularity) {
        this.granularity = granularity;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    // @Lob
    public URL getUrl() {
        return url;
    }

    // @Lob
    public ChartType getChartType() {
        return chartType;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    // @Lob
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    // @ManyToOne
    public BaseChart getParentChart() {
        return baseChart;
    }

    public void setParentChart(BaseChart parentChart) {
        this.baseChart = parentChart;
    }

    // @Id
    // @GeneratedValue(strategy = GenerationType.TABLE)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // @ManyToOne(fetch = FetchType.EAGER)
    public static ArrayList<String> getEntries() {
        return entries;
    }

    public static void setEntries(ArrayList<String> entries) {
        BaseChart.entries = entries;
    }

    public void setAuxillaryCharts(List<BaseChart> auxillaryCharts) {
        this.auxillaryCharts = auxillaryCharts;
    }

    public void setStartDate(Date sDate) {
        startDate = sDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date eDate) {
        endDate = eDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public JSONObject getQuoteforIndexJson(int i) {
        JSONObject quote = null;
        if (quotes != null && quotes.length >= i) {
            quote = (JSONObject) quotes[i];
        }
        return quote;
    }

    public String getQuoteForIndex(int i) {
        String quote = null;
        if (quotes != null && quotes.length >= i) {
            quote = (String) quotes[i];
        }
        return quote;
    }

    public Double getCloseAskForIndexJson(int i) {
        JSONObject quote = getQuoteforIndexJson(i);
        if (quote != null) {
            try {
                return (Double) quote.get("closeAsk");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void clearAuxillaryCharts() {
        auxillaryCharts.clear();
    }
}