package xianjie.shen.firstlinecode.CoolWeather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    /**
     * 处理返回的天气数据
     */
    public static void handleWeatherResponse(Context context,String response){
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String city = weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp2");//temp2为温度下限
            String temp2=weatherInfo.getString("temp1");//temp1为温度上限
            String weatherDesc =weatherInfo.getString("weather");
            String publish = weatherInfo.getString("ptime");
            saveWeatherInfo(context,city,weatherCode,temp1,temp2,weatherDesc,publish);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 将返回的json数据保存到SharedPreferences
     * @param context
     * @param city
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weahterDesc
     * @param publish
     */
    private static void saveWeatherInfo(Context context,String city, String weatherCode, String temp1, String temp2, String weahterDesc, String publish)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",city);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desc",weahterDesc);
        editor.putString("publish_time",publish);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }


}
