package capstone3.pollapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String [] sceneslist ={"Outdoor sports","Eating outside", "Sensitive people"};

    private EditText nameET;
    private EditText bdET;
    private RadioGroup genderRBG;
    private RadioButton genderRB;
    private Spinner scenesSP;
    private ArrayAdapter<String> sadapter = null;

    private String scenes;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        scenes = "Outdoor sports";

        nameET=(EditText)findViewById(R.id.et_name);
        bdET=(EditText)findViewById(R.id.et_birth);
        scenesSP=(Spinner)findViewById(R.id.sp_scenes);

        genderRBG = (RadioGroup)findViewById(R.id.gendergroup);

        //Scenes Spinner
        sadapter = new ArrayAdapter<String>(this,R.layout.spinner_item,sceneslist);
        sadapter.setDropDownViewResource(R.layout.dropdown_style);
        scenesSP.setAdapter(sadapter);
        scenesSP.setVisibility(View.VISIBLE);
        scenesSP.setOnItemSelectedListener(this);

        sharedPreferences = getSharedPreferences("PROFILES", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getBoolean("used",false))
        {
            nameET.setText(sharedPreferences.getString("usrname", "NONE"));
            bdET.setText(sharedPreferences.getString("userbirth", "NONE"));
            Log.i("gender",sharedPreferences.getString("usrgender", "NONE"));
            boolean ismale= (sharedPreferences.getString("usrgender", "NONE")).equals("Male");
            Log.i("gender",String.valueOf(ismale));
            genderRB = (RadioButton) findViewById(R.id.rbFemale);
            genderRB.setChecked(!ismale);
            genderRB = (RadioButton) findViewById(R.id.rbMale);
            genderRB.setChecked(ismale);
            String usrscenes = sharedPreferences.getString("usrscenes", "NONE");
            int i;
            for(i=0;i<3;i++)
            {
                if(sceneslist[i].equals(usrscenes))
                    break;
            }
            scenesSP.setSelection(i,true);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.sp_scenes:
                scenes = (String) parent.getSelectedItem();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void profile_submit(View v)
    {
        String usrname = nameET.getText().toString().trim();
        String usrbd = bdET.getText().toString().trim();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            format.setLenient(false);
            format.parse(usrbd);
        } catch (ParseException e) {
            convertSuccess = false;
        }
        if(convertSuccess && Integer.parseInt(usrbd.split("-")[0])>=year)
        {
            convertSuccess = false;
        }

        if(usrname.length()==0)
        {
            Toast.makeText(getApplicationContext(),"Please enter you name~",Toast.LENGTH_SHORT).show();
        }
        else if(!convertSuccess)
        {
            Toast.makeText(getApplicationContext(),"Please enter you birthday~",Toast.LENGTH_SHORT).show();
        }
        else {
            //save the usr profile
            editor.putString("usrname", usrname);
            editor.putString("userbirth", usrbd);
            int radiobuttonid = genderRBG.getCheckedRadioButtonId();
            genderRB = (RadioButton) findViewById(radiobuttonid);
            editor.putString("usrgender", genderRB.getText().toString().trim());
            editor.putString("usrscenes", scenes);
            editor.putBoolean("used", true);
            editor.apply();

            String location  = sharedPreferences.getString("usrlocation", "NONE");;
            Log.i("location",location);
            if(location.equals("NONE"))
            {
                //turn to LocationActivity
                startActivity(new Intent(ProfileActivity.this, LocationActivity.class));
                ProfileActivity.this.finish();
            }
            else
            {
                //turn to MainActivity
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                ProfileActivity.this.finish();
            }
        }
    }

    public void rbclick(View v)
    {
        int radiobuttonid = genderRBG.getCheckedRadioButtonId();
        genderRB = (RadioButton) findViewById(radiobuttonid);
    }
}
