package xianjie.shen.firstlinecode.CoolWeather.util;

import android.text.TextUtils;

import xianjie.shen.firstlinecode.CoolWeather.db.CoolWeatherDB;
import xianjie.shen.firstlinecode.CoolWeather.model.City;
import xianjie.shen.firstlinecode.CoolWeather.model.County;
import xianjie.shen.firstlinecode.CoolWeather.model.Province;

/**
 * Created by shen on 2016/6/26.
 */
public class DataUtil
{
    /**
     * 处理服务端返回的省级数据,并存入数据库
     *
     * @param db
     * @param response
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB db, String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0)
            {
                for (String s : allProvinces)
                    {
                    String[] array = s.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //存入数据库中
                    db.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 处理服务端返回的省级数据，并存入数据库
     */
    public static boolean handleCitiesResponse(CoolWeatherDB db, String response, int provinceId)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0)
            {
                for (String s : allCities)
                {
                    String[] cityArray = s.split("\\|");
                    City city = new City();
                    city.setCityCode(cityArray[0]);
                    city.setCityName(cityArray[1]);
                    city.setProvinceId(provinceId);
                    db.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 处理服务端返回的省级数据，并存入数据库
     */
    public static boolean handleCountiesResponse(CoolWeatherDB db, String response, int cityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0)
            {
                for (String s : allCounties)
                {
                    String[] countyArray = s.split("\\|");
                    County county = new County();
                    county.setCountyCode(countyArray[0]);
                    county.setCountyName(countyArray[1]);
                    county.setCityId(cityId);
                    db.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

}
