package xianjie.shen.firstlinecode.CoolWeather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import xianjie.shen.firstlinecode.R;

/**
 * Created by shen on 2016/6/26.
 */
public class MyAdapter extends BaseAdapter
{

    private Context mContext;
    private List<?> mDatas;
    private LayoutInflater mInflater;
    private int mLayoutId;

    public MyAdapter(Context context, List<?> datas, int layoutId)
    {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return mDatas.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mLayoutId, parent, false);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_name);


            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置数据
        holder.tv.setText("");
        holder.tv.setText(mDatas.get(pos).toString());
        return convertView;
    }

    private class ViewHolder
    {
        TextView tv;
    }
}
