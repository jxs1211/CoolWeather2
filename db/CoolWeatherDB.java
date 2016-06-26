package xianjie.shen.firstlinecode.CoolWeather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import xianjie.shen.firstlinecode.CoolWeather.model.City;
import xianjie.shen.firstlinecode.CoolWeather.model.County;
import xianjie.shen.firstlinecode.CoolWeather.model.Province;

/**
 * Created by shen on 2016/6/20.
 */
public class CoolWeatherDB
{
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本
     */
    public static final int DB_VERSION = 1;
    public static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context)
    {
        CoolWeatherOpenHelper helper = new CoolWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = helper.getWritableDatabase();
    }

    /**
     * 实例化coolWeatherDB
     *
     * @param context
     * @return
     */
    public synchronized static CoolWeatherDB getInstance(Context context)
    {
        if (coolWeatherDB == null)
        {
            coolWeatherDB = new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 保存Province实例到数据库
     *
     * @param province
     */
    public void saveProvince(Province province)
    {
        if (province != null)
        {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国省份信息
     */
    public List<Province> getProvincesFromDB()
    {
        List<Province> provinces = new ArrayList<Province>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst())
        {
            do
            {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinces.add(province);
            } while (cursor.moveToNext());
        }
        return provinces;
    }

    /**
     * 保存City实例存储到数据库
     */
    public void saveCity(City city)
    {
        if (city != null)
        {
            ContentValues values = new ContentValues();
            values.put("province_id", city.getProvinceId());
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取city信息
     */
    public List<City> getCitiesFromDB(int provinceId)
    {
        List<City> cities = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst())
        {
            do
            {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                cities.add(city);
            } while (cursor.moveToNext());
        }

        return cities;
    }

    /**
     * 保存County实例到数据库
     */
    public void saveCounty(County county)
    {
        if (county != null)
        {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     * 读取county的所有数据
     */
    public List<County> getCountiesFromDB(int cityId)
    {
        List<County> counties = new ArrayList<County>();
        Cursor cursor = db.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst())
        {
            do
            {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                counties.add(county);
            } while (cursor.moveToNext());
        }

        return counties;
    }

}
