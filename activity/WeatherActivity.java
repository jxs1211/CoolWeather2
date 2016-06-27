package xianjie.shen.firstlinecode.CoolWeather.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import xianjie.shen.firstlinecode.CoolWeather.util.BaseActivity;
import xianjie.shen.firstlinecode.CoolWeather.util.Constants;
import xianjie.shen.firstlinecode.CoolWeather.util.DataUtil;
import xianjie.shen.firstlinecode.CoolWeather.util.NetUtil;
import xianjie.shen.firstlinecode.R;

/**
 * Created by shen on 2016/6/26.
 */
public class WeatherActivity extends BaseActivity
{
    private TextView mTitle;
    private TextView mPublish;
    private TextView mDate;
    private TextView mWeatherDesc;
    private TextView mTemp1;
    private TextView mTemp2;
    private ImageView mRefresh;
    private String mCountyCode;
    private LinearLayout mWeatherInfoLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coolweather_weather);

        mCountyCode = getIntent().getStringExtra("county_code");

        initViews();
        initDatas();
        initEvents();

    }

    private void initViews()
    {
        mWeatherInfoLayout = (LinearLayout) findViewById(R.id.ll_weather_info_layout);
        mTitle = (TextView) findViewById(R.id.tv_head_title);
        mPublish = (TextView) findViewById(R.id.tv_publish);
        mDate = (TextView) findViewById(R.id.tv_date);
        mWeatherDesc = (TextView) findViewById(R.id.tv_weather);
        mTemp1 = (TextView) findViewById(R.id.tv_temp1);
        mTemp2 = (TextView) findViewById(R.id.tv_temp2);
        mRefresh = (ImageView) findViewById(R.id.iv_head_right);

        if (!TextUtils.isEmpty(mCountyCode))
        {// 有县级代号时就去查询天气
            mPublish.setText("同步中...");
            mWeatherInfoLayout.setVisibility(View.INVISIBLE);
            mTitle.setVisibility(View.INVISIBLE);
            queryWeatherCode(mCountyCode);
        } else
        {// 没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    private void initDatas()
    {


    }

    private void initEvents()
    {

    }

    /**
     * 根据countyCode查询WeatherCode
     *
     * @param mCountyCode
     */
    private void queryWeatherCode(String mCountyCode)
    {
        String address = Constants.getAddrWithCode(mCountyCode);
        queryFromServer(address, "countyCode");
    }

    private void queryFromServer(String address, final String type)
    {
        NetUtil.doGetByHttpURLConnection(address, new NetUtil.CallBack()
        {
            @Override
            public void success(String response)
            {
                if (type.equals("countyCode"))
                {
                    if (!TextUtils.isEmpty(response))
                    {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2)
                        {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if (type.equals("weatherCode"))
                {
                    DataUtil.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void error(Exception e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mPublish.setText("同步失败");
                    }
                });
            }
        });
    }

    private void queryWeatherInfo(String weatherCode)
    {
        if (weatherCode != null)
        {
            String address = Constants.getAddrWithCodeHTML(weatherCode);
            queryFromServer(address, "weatherCode");
        }
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mTitle.setText(prefs.getString("city_name", ""));
        mPublish.setText("今天" + prefs.getString("publish_time", "") + "点发布");
        mDate.setText(prefs.getString("current_date", ""));
        mWeatherDesc.setText(prefs.getString("weather_desc", ""));
        mTemp1.setText(prefs.getString("temp1", ""));
        mTemp2.setText(prefs.getString("temp2", ""));
        mTitle.setVisibility(View.VISIBLE);
        mWeatherInfoLayout.setVisibility(View.VISIBLE);
    }
}
