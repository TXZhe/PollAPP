package capstone3.pollapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView comicIV;
    private String url;
    private SharedPreferences.Editor editor;

    private Handler myHandler;

    private String usrname;
    private String usrgender;
    private String usrage;
    private String usrscenes;
    private String usrlocation;
    private String usrlatlng;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

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
        comicIV = (ImageView)findViewById(R.id.iv_comic);
        url = "https://www.lego.com/r/www/r/catalogs/-/media/catalogs/characters/lbm%20characters/primary/70900_1to1_batman_360_480.png?l.r=1668006940";

        SharedPreferences preferences = getSharedPreferences("PROFILES", Context.MODE_PRIVATE);
        editor = preferences.edit();

        usrname = preferences.getString("usrname", "NONE");
        usrgender = preferences.getString("usrgender", "NONE");
        usrscenes = preferences.getString("usrscenes", "NONE");
        usrlocation = preferences.getString("usrlocation", "NONE");
        usrlatlng = preferences.getString("usrlatlng", "NONE");

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


        //make a json
        JSONObject jsonProfile = new JSONObject();
        try {
            jsonProfile.put("usrname", usrname);
            jsonProfile.put("userage", usrage);
            jsonProfile.put("usrgender", usrgender);
            jsonProfile.put("usrscenes", usrscenes);
            jsonProfile.put("usrlocation", usrlocation);
            jsonProfile.put("usrlatlng", usrlatlng);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("json",jsonProfile.toString());

        myHandler=new Handler(getApplicationContext().getMainLooper());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                final Bitmap img = getPic(url);//下载
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        comicIV.setImageBitmap(img);
                    }
                });
                Looper.loop();
            }
        });
        thread.start();
    }
    /***********************new****************************/


    /**
     *  Get picture from server
     * @param url
     * @return bitmap
     */
    public Bitmap getPic(String url) {
        //获取okHttp对象get请求
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            ResponseBody body = client.newCall(request).execute().body();
            InputStream in = body.byteStream();
            Bitmap bitmap = BitmapFactory.decodeStream(in);
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
