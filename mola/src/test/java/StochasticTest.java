import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class StochasticTest {
    List<Double> highList = new ArrayList<Double>();
    List<Double> lowList = new ArrayList<Double>();
    List<Double> currentList = new ArrayList<Double>();
    double[] hl = new double[]{127.01, 127.62, 126.59, 127.35, 128.17, 128.43, 127.37, 126.42, 126.90,
            126.85, 125.65, 125.72, 127.16, 127.72, 127.69, 128.22, 128.27, 128.09, 128.27, 127.74, 128.77, 129.29,
            130.06, 129.12, 129.29, 128.47, 128.09, 128.65, 129.14, 128.64};
    double[] ll = new double[]{125.36, 126.16, 124.93, 126.09, 126.82, 126.48, 126.03, 124.83, 126.39,
            125.72, 124.56, 124.57, 125.07, 126.86, 126.63, 126.80, 126.71, 126.80, 126.13, 125.92, 126.99, 127.81,
            128.47, 128.06, 127.61, 127.60, 127.00, 126.90, 127.49, 127.40};
    double[] cl = new double[]{127.29, 127.18, 128.01, 127.11, 127.73, 127.06, 127.33, 128.71, 127.87, 128.58, 128.60, 127.93,
            128.11, 127.60, 127.60, 128.69, 128.27};
    Double base = new Double(3.00);
    static final int MAX = 30;
    static final int LOOKBACK = 14;

    public static void main(String[] args) {
        StochasticTest st = new StochasticTest();
        st.init();
        st.computeStochastic();
    }

    public void init() {
        highList = new ArrayList(Arrays.asList(127.01, 127.62, 126.59, 127.35, 128.17, 128.43, 127.37, 126.42, 126.90,
                126.85, 125.65, 125.72, 127.16, 127.72, 127.69, 128.22, 128.27, 128.09, 128.27, 127.74, 128.77, 129.29,
                130.06, 129.12, 129.29, 128.47, 128.09, 128.65, 129.14, 128.64));
        lowList = new ArrayList(Arrays.asList(125.36, 126.16, 124.93, 126.09, 126.82, 126.48, 126.03, 124.83, 126.39,
                125.72, 124.56, 124.57, 125.07, 126.86, 126.63, 126.80, 126.71, 126.80, 126.13, 125.92, 126.99, 127.81,
                128.47, 128.06, 127.61, 127.60, 127.00, 126.90, 127.49, 127.40));
        currentList = new ArrayList(Arrays.asList(127.29, 127.18, 128.01, 127.11, 127.73, 127.06, 127.33, 128.71, 127.87, 128.58, 128.60, 127.93,
                128.11, 127.60, 127.60, 128.69, 128.27));
    }

    public void computeStochastic() {
        com.tictactec.ta.lib.Core core = new Core();
        MInteger outBegIdx = new MInteger();
        MInteger outBNElement = new MInteger();
        double[] outSlowK = new double[200];
        double[] outSlowD = new double[200];

        for (int i = 0; i < hl.length / 2; i++) {
            double temp = hl[i];
            hl[i] = hl[hl.length - i - 1];
            hl[hl.length - i - 1] = temp;
        }

        for (int i = 0; i < ll.length / 2; i++) {
            double temp = ll[i];
            ll[i] = ll[ll.length - i - 1];
            ll[ll.length - i - 1] = temp;
        }

        for (int i = 0; i < cl.length / 2; i++) {
            double temp = cl[i];
            cl[i] = cl[cl.length - i - 1];
            cl[cl.length - i - 1] = temp;
        }


        core.stoch(0, 16, hl, ll, cl, 14, 3, MAType.Sma, 2, MAType.Sma, outBegIdx, outBNElement, outSlowK, outSlowD);
        for (int i = 0; i < outBNElement.value; ++i) {
            System.out.println(outSlowK[i]);
        }
//		public RetCode stoch( int startIdx,
//			      int endIdx,
//			      double inHigh[],
//			      double inLow[],
//			      double inClose[],
//			      int optInFastK_Period,
//			      int optInSlowK_Period,
//			      MAType optInSlowK_MAType,
//			      int optInSlowD_Period,
//			      MAType optInSlowD_MAType,
//			      MInteger outBegIdx,
//			      MInteger outNBElement,
//			      double outSlowK[],
//			      double outSlowD[] )
//			   {
        Double k = 0.0;
        // %K = 100[(C - L14)/(H14 - L14)]
        for (int i = currentList.size() - 1; i >= 0; --i) {
            Double current = currentList.get(i);
            Double hh = getHighestHigh(i + 14);
            Double ll = getLowestLow(i + 14);
            k = ((current - ll) / (hh - ll)) * 100;
            System.out.println("**k**" + k + " current:" + current + " ll: " + ll + " hh " + hh);
        }
    }

    public Double getLowestLow(int currentPos) {
        Double low = null;
        int stopPos = currentPos - LOOKBACK - 1;
        for (int i = currentPos - 1; i > stopPos; --i) {
            if (low == null || low > lowList.get(i)) {
                low = lowList.get(i);
            }
        }
        return low;
    }

    public Double getHighestHigh(int currentPos) {
        Double high = null;
        int stopPos = currentPos - LOOKBACK - 1;
        for (int i = currentPos - 1; i > stopPos; --i) {
            if (high == null || high < highList.get(i)) {
                high = highList.get(i);
            }
        }
        return high;
    }

    public void testHighLow(List<Double> list, int currentPos, boolean greater, Double value) {
        try {
            for (int i = currentPos; i < list.size(); ++i) {
                if (greater) {
                    if (value < list.get(i)) {
                        throw new Exception("List index: " + i + " value: " + list.get(i) + " is greater than " + value);
                    }
                } else {
                    if (value > list.get(i)) {
                        throw new Exception("List index: " + i + " value: " + list.get(i) + " is less than " + value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    {
        highList = new ArrayList(Arrays.asList(127.01, 127.62, 126.59, 127.35, 128.17, 128.43, 127.37, 126.42, 126.90,
                126.85, 125.65, 125.72, 127.16, 127.72, 127.69, 128.22, 128.27, 128.09, 128.27, 127.74, 128.77, 129.29,
                130.06, 129.12, 129.29, 128.47, 128.09, 128.65, 129.14, 128.64));
        lowList = new ArrayList(Arrays.asList(125.36, 126.16, 124.93, 126.09, 126.82, 126.48, 126.03, 124.83, 126.39,
                125.72, 124.56, 124.57, 125.07, 126.86, 126.63, 126.80, 126.71, 126.80, 126.13, 125.92, 126.99, 127.81,
                128.47, 128.06, 127.61, 127.60, 127.00, 126.90, 127.49, 127.40));
    }
}
