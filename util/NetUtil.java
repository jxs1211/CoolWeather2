package xianjie.shen.firstlinecode.CoolWeather.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import xianjie.shen.firstlinecode.Chapter10_BestPractise.Bean;
import xianjie.shen.firstlinecode.util.Constants;
import xianjie.shen.firstlinecode.util.SAXContentHandler;

/**
 * Created by shen on 2016/6/19.
 */
public class NetUtil
{
    public static final int SHOW_RESPONSE = 0;
    public static final int HTTPCLIENT_GET = 1;
    public static final int HTTPCLIENT_POST = 2;
    public static final int PARSE_XML_BY_PULL = 3;
    public static final int PARSE_XML_BY_SAX = 4;
    public static final int PARSE_JSON_BY_JSONOBJECT = 5;
    public static final int PARSE_JSON_BY_GSON = 6;

    public interface CallBack
    {
        void success(String response);

        void error(Exception e);
    }

    public static void doGetByHttpURLConnection(final String urlStr, final CallBack callBack)
    {
//        if (!isNetworkAvailable())
//        {
//            Toast.makeText(MyApplication.getContext(), "network is unavailable", Toast.LENGTH_SHORT).show();
//            return;
//        }
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                InputStream is = null;
                BufferedReader reader = null;
                try
                {
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    is = connection.getInputStream();
                    //对输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                    {
                        response.append(line);
                    }
                    if (callBack != null)
                    {
                        callBack.success(response.toString());
                    }
                    is.close();
                    reader.close();
                } catch (Exception e)
                {
                    if (callBack != null) callBack.error(e);
                    e.printStackTrace();
                } finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }

    private static boolean isNetworkAvailable()
    {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isAvailable())
        {
            return true;
        } else
        {
            return false;
        }
    }

    public static void doPostByHttpURLConnection(final String urlStr, final CallBack callBack)
    {
//        if (!isNetworkAvailable())
//        {
//            Toast.makeText(MyApplication.getContext(), "network is unavailable", Toast.LENGTH_SHORT).show();
//            return;
//        }
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpURLConnection connection = null;
                DataOutputStream dos = null;
                BufferedReader reader = null;
                try
                {
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    dos = new DataOutputStream(connection.getOutputStream());
                    dos.writeBytes("postStr");//postStr的写法：username=1&password=1

                } catch (IOException e)
                {
                    e.printStackTrace();
                } finally
                {
                    if (connection != null)
                    {
                        connection.disconnect();
                    }
                }

            }
        }).start();
    }

    public static void doRequestByHttpClient(final Context context, final int requestType, final String urlStr, String useName, String passwd, final CallBack callBack, final int parseDataType, final boolean isJsonObject)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                HttpClient httpClient = null;
                HttpResponse httpResponse = null;
                try
                {
                    switch (requestType)
                    {
                        case HTTPCLIENT_GET:
                            httpClient = new DefaultHttpClient();
                            HttpGet httpGet = new HttpGet(urlStr);
                            httpResponse = httpClient.execute(httpGet);
                            break;
                        case HTTPCLIENT_POST:
                            httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost(urlStr);
                            List<NameValuePair> parmas = new ArrayList<NameValuePair>();
                            parmas.add(new BasicNameValuePair("username", "1"));
                            parmas.add(new BasicNameValuePair("passwd", "1"));
                            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parmas);
                            httpPost.setEntity(entity);
                            httpResponse = httpClient.execute(httpPost);
                            break;
                    }
                    if (httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        Log.d(Constants.BP, "200");
                        //ok
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");//返回内容有中文时，设置为utf-8

                        //判断数据返回类型，以及数据的解析方式
                        if (parseDataType == PARSE_XML_BY_PULL)
                        {
                            parseXMLWithPull(response);
                        } else if (parseDataType == PARSE_XML_BY_SAX)
                        {
                            parseXMLWithSAX(response);
                        } else if (parseDataType == PARSE_JSON_BY_JSONOBJECT)
                        {
                            parseJSONWithJSONObject(response);
                        } else if (parseDataType == PARSE_JSON_BY_GSON)
                        {
                            parseJSONWithGSON(response, isJsonObject);
                        }

                        if (callBack != null) callBack.success(response.toString());
                    } else
                    {
                        Toast.makeText(context, "request fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e)
                {
                    if (callBack != null) callBack.error(e);
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static void parseJSONWithGSON(String response, boolean isJsonObject)
    {
        Gson gson = new Gson();
        Log.d(Constants.BP, "parseJSONWithGSON");
        if (isJsonObject)
        {
            //parse jsonObject to java bean
            Bean bean = gson.fromJson(response, Bean.class);
            Log.d(Constants.BP, "id=" + bean.getId() + " " + "name=" + bean.getName() + " " + "version=" + bean.getVersion());
        } else
        {
            //parse jsonArray to java beans
            List<Bean> beans = gson.fromJson(response, new TypeToken<List<Bean>>()
            {
            }.getType());
            for (Bean bean : beans)
            {
                Log.d(Constants.BP, "id=" + bean.getId() + " " + "name=" + bean.getName() + " " + "version=" + bean.getVersion());
            }
        }
    }

    private static void parseJSONWithJSONObject(String jsonData)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d(Constants.BP, "id=" + jsonObject.getString("id") + " " + "name=" + jsonObject.getString("version") + " " + "version=" + jsonObject.getString("name"));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private static void parseXMLWithPull(String xmlData)
    {
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                String nodeName = xmlPullParser.getName();
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                        if (nodeName.equals("id"))
                        {
                            id = xmlPullParser.nextText();
                        } else if (nodeName.equals("name"))
                        {
                            name = xmlPullParser.nextText();
                        } else if (nodeName.equals("version"))
                        {
                            version = xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (nodeName.equals("app"))
                        {
                            Log.d(Constants.BP, "id=" + id + " " + "name=" + name + " " + "version=" + version);
                        }
                        break;
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {

        }
    }

    private static void parseXMLWithSAX(String xmlData)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader = factory.newSAXParser().getXMLReader();

            SAXContentHandler saxContentHandler = new SAXContentHandler();
            xmlReader.setContentHandler(saxContentHandler);
            xmlReader.parse(new InputSource(new StringReader(xmlData)));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
