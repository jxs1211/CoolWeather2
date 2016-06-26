package xianjie.shen.firstlinecode.CoolWeather.util;

/**
 * Created by shen on 2016/6/26.
 */
public class Constants
{
    public static final String ADDR = "http://www.weather.com.cn/data/list3/city.xml";
    public static final String ADDR_NO_XML = "http://www.weather.com.cn/data/list3/city";

    public static final String TAG_BP_COOLWEATHER = "COOLWEATHER";

    public static String getAddrWithCode(String code)
    {
        return ADDR_NO_XML + code + ".xml";
    }
}
