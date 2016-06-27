package xianjie.shen.firstlinecode.CoolWeather.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import xianjie.shen.firstlinecode.CoolWeather.receiver.AutoUpdateReceiver;
import xianjie.shen.firstlinecode.CoolWeather.util.Constants;
import xianjie.shen.firstlinecode.CoolWeather.util.DataUtil;
import xianjie.shen.firstlinecode.CoolWeather.util.NetUtil;
import xianjie.shen.firstlinecode.CoolWeather.util.TimerUtil;

/**
 * Created by shen on 2016/6/27.
 */
public class AutoUpdateService extends Service
{
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                updateWeather();
            }
        }).start();

        TimerUtil.setTimerTask(this, 8, AutoUpdateReceiver.class);

//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent1, 0);
//        long interval = 8 * 60 * 60 * 1000;//8小时的毫秒数
//        long triggerTime = SystemClock.elapsedRealtime() + interval;
//        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weather_code = prefs.getString("weather_code", "");
        String address = Constants.getAddrWithCodeHTML(weather_code);
        NetUtil.doGetByHttpURLConnection(address, new NetUtil.CallBack()
        {
            @Override
            public void success(String response)
            {//存储定时任务获取的服务器返回数据
                DataUtil.handleWeatherResponse(AutoUpdateService.this, response);
            }

            @Override
            public void error(Exception e)
            {

            }
        });
    }
}
