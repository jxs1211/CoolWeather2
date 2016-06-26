package xianjie.shen.firstlinecode.CoolWeather.util;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import xianjie.shen.firstlinecode.util.ActivityCollector;

public class BaseActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
