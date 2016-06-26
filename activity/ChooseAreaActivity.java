package xianjie.shen.firstlinecode.CoolWeather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import xianjie.shen.firstlinecode.CoolWeather.adapter.MyAdapter;
import xianjie.shen.firstlinecode.CoolWeather.db.CoolWeatherDB;
import xianjie.shen.firstlinecode.CoolWeather.model.City;
import xianjie.shen.firstlinecode.CoolWeather.model.County;
import xianjie.shen.firstlinecode.CoolWeather.model.Province;
import xianjie.shen.firstlinecode.CoolWeather.util.BaseActivity;
import xianjie.shen.firstlinecode.CoolWeather.util.Constants;
import xianjie.shen.firstlinecode.CoolWeather.util.DataUtil;
import xianjie.shen.firstlinecode.CoolWeather.util.MyApplication;
import xianjie.shen.firstlinecode.CoolWeather.util.NetUtil;
import xianjie.shen.firstlinecode.R;

/**
 * Created by shen on 2016/6/26.
 */
public class ChooseAreaActivity extends BaseActivity
{
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTextView;
    private ListView mListView;
    private MyAdapter mAdapter;
    private ListAdapter mArrayAdapter;
    private CoolWeatherDB db;
    private List<String> dataList = new ArrayList<String>();

    /**
     * 省列表
     */
    private List<Province> mProvinceList;
    /**
     * 选中的省
     */
    private Province selectedProvince;
    /**
     * 市列表
     */
    private List<City> mCityList;
    /**
     * 选中的市
     */
    private City selectedCity;
    /**
     * 县列表
     */
    private List<County> mCountyList;
    /**
     * 选中的县
     */
    private County selectedCounty;
    /**
     * 当前选中的级别
     */
    private int current_level;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coolweather_choose_area);

        db = CoolWeatherDB.getInstance(this);

        initViews();
        initDatas();
        initEvents();

    }

    private void initDatas()
    {
        mAdapter = new MyAdapter(this, dataList, R.layout.coolweather_list_item);
        mListView.setAdapter(mAdapter);
    }

    private void initViews()
    {
        mListView = (ListView) findViewById(R.id.lv_list);
        mTextView = (TextView) findViewById(R.id.tv_title);
//        mProgressDialog = new ProgressDialog(this);
    }

    private void initEvents()
    {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l)
            {
                if (current_level == LEVEL_PROVINCE)
                {
                    selectedProvince = mProvinceList.get(pos);
                    queryCities();
                } else if (current_level == LEVEL_COUNTY)
                {
                    selectedCounty = mCountyList.get(pos);
                    queryCounties();
                }
            }
        });
        //默认加载省的列表
        queryProvince();
    }

    /**
     * 查询选中省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvince()
    {
        mProvinceList = db.getProvincesFromDB();
        if (mProvinceList.size() > 0)
        {
            dataList.clear();
            for (Province province : mProvinceList)
            {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText("中国");
            current_level = LEVEL_PROVINCE;
        } else
        {
            queryFromServer(null, "province");
        }
    }

    private void queryFromServer(String code, final String type)
    {
        String address;
        if (!TextUtils.isEmpty(code))
        {
            address = Constants.getAddrWithCode(code);
        } else
        {
            address = Constants.ADDR;
        }

        showProgressDialog();

        NetUtil.doGetByHttpURLConnection(address, new NetUtil.CallBack()
        {
            @Override
            public void success(String response)
            {
                boolean result = false;
                if (type.equals("province"))
                {//拿到返回数据，存入数据库
                    result = DataUtil.handleProvincesResponse(db, response);
                } else if (type.equals("city"))
                {//拿到返回数据，存入数据库
                    result = DataUtil.handleCitiesResponse(db, response, selectedProvince.getId());
                } else if (type.equals("county"))
                {//拿到返回数据，存入数据库
                    result = DataUtil.handleCountiesResponse(db, response, selectedCity.getId());
                }
                if (result)
                {
                    //数据成功保存到数据库后，查询数据
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            closeProgressDialog();
                            if (type.equals("province"))
                            {
                                queryProvince();
                            } else if (type.equals("city"))
                            {
                                queryCities();
                            } else if (type.equals("county"))
                            {
                                queryCounties();
                            }
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
                        closeProgressDialog();
                        Toast.makeText(MyApplication.getContext(), "load data fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示加载框
     */
    private void showProgressDialog()
    {
        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /**
     * 关闭加载框
     */
    private void closeProgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.dismiss();
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties()
    {
        mCountyList = db.getCountiesFromDB(selectedCity.getId());
        if (mCountyList != null && mCountyList.size() > 0)
        {
            dataList.clear();
            for (County county : mCountyList)
            {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText(selectedCity.getCityName());
        } else
        {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities()
    {
        mCityList = db.getCitiesFromDB(selectedProvince.getId());
        if (mCityList != null && mCityList.size() > 0)
        {
            dataList.clear();
            for (City city : mCityList)
            {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText(selectedProvince.getProvinceName());
            current_level = LEVEL_CITY;
        } else
        {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }

    }

    /**
     * 返回键处理
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if (current_level == LEVEL_COUNTY)
            {
                queryCities();
            } else if (current_level == LEVEL_CITY)
            {
                queryProvince();
            } else
            {
                continueClickExitApp();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void continueClickExitApp()
    {
        Log.e(Constants.TAG_BP_COOLWEATHER, System.currentTimeMillis() + "");
        if (System.currentTimeMillis() - mExitTime > 2000L)
        {//大于2s弹出toast
            Toast.makeText(MyApplication.getContext(), R.string.exit_tips_one_more_time, Toast.LENGTH_SHORT).show();
            this.mExitTime = System.currentTimeMillis();// 记录下这个点击的时间点
        } else//2s内连按2次就退出app
        {
            finish();
            System.exit(0);
        }
    }
}
