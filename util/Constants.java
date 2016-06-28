package xianjie.shen.firstlinecode.CoolWeather.util;

/**
 * Created by shen on 2016/6/26.
 */
public class Constants
{
    public static final String ADDR = "http://www.weather.com.cn/data/list3/city.xml";
    public static final String ADDR_NO_XML = "http://www.weather.com.cn/data/list3/city";
    public static final String ADDR_NO_HTML = "http://www.weather.com.cn/data/cityinfo/";
    public static final String YOUMI_APP_ID = "5074b84092130bf0";
    public static final String YOUMI_APP_KEY = "58a28906468daf63";

    public static final String TAG_BP_COOLWEATHER = "COOLWEATHER";

    public static String getAddrWithCode(String code)
    {
        return ADDR_NO_XML + code + ".xml";
    }

    public static String getAddrWithCodeHTML(String code)
    {
        return ADDR_NO_HTML + code + ".html";
    }
}
