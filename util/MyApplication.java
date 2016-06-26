package xianjie.shen.firstlinecode.CoolWeather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by shen on 2016/6/19.
 */
public class MyApplication extends Application
{
    private static Context mContext;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext()
    {
        return mContext;
    }
}
