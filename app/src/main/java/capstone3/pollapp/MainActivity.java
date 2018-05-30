package capstone3.pollapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView comicIV;
    private TextView o3TV;
    private TextView pm10TV;
    private TextView pm25TV;
    private TextView no2TV;
    private TextView aqiTV;
    private LinearLayout mHiddenLayout;
    private LinearLayout guiLayout;
    private ImageButton locationBT;
    private ImageButton profileBT;
    private ImageView dataBT;

    private View[] GUI= new View[3];

    private int mHiddenViewMeasuredHeight;

    private String urlpic = "https://shit-205415.appspot.com/pictures";
    private String urldata = "https://shit-205415.appspot.com/data";

    private Handler myHandler;

    private String usrname;
    private String usrgender;
    private String usrage;
    private String usrscenes;
    private String usrlocation;
    private String usrlat;
    private String usrlng;

    private JSONObject jsonProfile;

    public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //start new
        comicIV = (ImageView) findViewById(R.id.iv_comic);
        o3TV = (TextView) findViewById(R.id.tv_o3);
        pm10TV = (TextView) findViewById(R.id.tv_pm10);
        pm25TV = (TextView) findViewById(R.id.tv_pm25);
        no2TV = (TextView) findViewById(R.id.tv_no2);
        aqiTV = (TextView) findViewById(R.id.tv_aqi);
        mHiddenLayout = (LinearLayout) findViewById(R.id.ll_hidden);
        guiLayout = (LinearLayout) findViewById(R.id.ll_gui);
        locationBT = (ImageButton) findViewById(R.id.bt_location);
        profileBT = (ImageButton) findViewById(R.id.bt_profile);
        dataBT = (ImageView) findViewById(R.id.bt_data);

        GUI[0] = locationBT;
        GUI[1] = profileBT;
        GUI[2] = dataBT;

        float mDensity = getResources().getDisplayMetrics().density;
        mHiddenViewMeasuredHeight = (int) (mDensity * 180 + 0.5);

        mHiddenLayout.setVisibility(View.GONE);

        SharedPreferences preferences = getSharedPreferences("PROFILES", Context.MODE_PRIVATE);

        usrname = preferences.getString("usrname", "NONE");
        usrgender = preferences.getString("usrgender", "NONE");
        usrscenes = preferences.getString("usrscenes", "NONE");
        usrlocation = preferences.getString("usrlocation", "NONE");
        usrlat = preferences.getString("usrlat", "NONE");
        usrlng = preferences.getString("usrlng", "NONE");

        this.setTitle(usrlocation);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String usrbd = preferences.getString("userbirth", "NONE");
        String[] usrymd = usrbd.split("-");
        if(month<Integer.parseInt(usrymd[1]) ||
                (month==Integer.parseInt(usrymd[1])&& day<Integer.parseInt(usrymd[2])))
        {
            usrage = Integer.toString(year - Integer.parseInt(usrymd[0])-1);
        }
        else
        {
            usrage = Integer.toString(year - Integer.parseInt(usrymd[0]));
        }

        //nav header name
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderName = (TextView)headerView.findViewById(R.id.nav_header_name);
        String navHeaderNamest;
        if(usrgender.equals("Male"))
        {
            navHeaderNamest = "Mr."+usrname;
        }
        else
        {
            navHeaderNamest = "Mr."+usrname;
        }
        navHeaderName.setText(navHeaderNamest);

        //make a json
        jsonProfile = new JSONObject();
        try {
            jsonProfile.put("usrname", usrname);
            jsonProfile.put("userage", usrage);
            jsonProfile.put("usrgender", usrgender);
            jsonProfile.put("usrscenes", usrscenes);
            jsonProfile.put("usrlocation", usrlocation);
            jsonProfile.put("usrlat", usrlat);
            jsonProfile.put("usrlng", usrlng);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("json",jsonProfile.toString());

        myHandler=new Handler(getApplicationContext().getMainLooper());
        Thread threadpic = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final Bitmap img = getPic(urlpic,jsonProfile.toString());//下载
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        comicIV.setImageBitmap(img);
                    }
                });
                Looper.loop();
            }
        });
        threadpic.start();

        Thread threaddata = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final JSONObject data = getData(urldata,jsonProfile.toString());//下载
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(data!=null){
                            try {
                                aqiTV.setText(String.format("AQI: %.2f",data.getDouble("aqi")));
                                no2TV.setText(String.format("NO2: %.2f",data.getDouble("no2")));
                                pm25TV.setText(String.format("PM2.5: %.2f",data.getDouble("pm25")));
                                pm10TV.setText(String.format("PM10: %.2f",data.getDouble("pm10")));
                                o3TV.setText(String.format("O3: %.2f",data.getDouble("o3")));
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Looper.loop();
            }
        });
        threaddata.start();
    }
    /***********************new****************************/

    /**
     *  Get data from server
     * @param url
     * @return JSON object
     */

    public JSONObject getData(String url, String json){
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            ResponseBody response = client.newCall(request).execute().body();
            String in = response.string();
            Log.i("data",in);
            try {
                JSONObject data = new JSONObject(in);
                return data;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  Get picture from server
     * @param url
     * @return bitmap
     */
    public Bitmap getPic(String url, String json) {
        //获取okHttp对象post请求
        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            ResponseBody response = client.newCall(request).execute().body();
            InputStream in = response.byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            Log.i("pic","Okay");
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Profile button
     */
    public void goProfile(View v)
    {
        //turn to ProfileActivity
        startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        MainActivity.this.finish();
    }


    /**
     * location button
     */
    public void goLocation(View v)
    {
        //turn to LocationActivity
        startActivity(new Intent(MainActivity.this, LocationActivity.class));
        MainActivity.this.finish();
    }

    /**
     * detail data button
     */
    public void Gstar(View v){
        if(mHiddenLayout.getVisibility() == View.GONE) {
            animateOpen(mHiddenLayout);
        }
        else
        {
            animateClose(mHiddenLayout);
        }

    }

    /**
     * detail data display animation
     */
    private void animateOpen(View v) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, 0,
                mHiddenViewMeasuredHeight);
        animator.start();

    }

    private void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);

            }
        });
        return animator;
    }
    /**
     * detail data display animation end
     */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            ShouldHideButton(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private  void ShouldHideButton(MotionEvent event){
        boolean result = true;
        for (View v:GUI){
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom){
                result = false;
                break;
            }
        }
        if(result){
            if(guiLayout.getVisibility() == View.GONE) {
                guiLayout.setVisibility(View.VISIBLE);
            }
            else{
                guiLayout.setVisibility(View.GONE);
            }
        }
    }

    /*******************end new****************************/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
