import java.text.SimpleDateFormat;

public class ScatchPAd {
    public static void main(String[] args) throws Exception {
        String date = "2014-02-14T12:00:00";
//		String in = "2014-07-21T16:35:27.000Z";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        System.out.println(sdf.parse(date).getTime());

        double value = 1.836734693877551;
        String[] split = String.valueOf(value).split("\\.");
        double integer = Double.parseDouble(split[0]);
        if(split[1].length() > 8){
            split[1] = split[1].substring(0, 8);
        }
        double fraction = Math.abs(integer - value);
        System.out.println(((fraction * 60)*1000));
        System.out.println((((integer * 60) * 1000) + ((fraction * 60)*1000)));
    }
}
