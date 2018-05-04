package capstone3.pollapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String [] genderlist ={"Male","Female"};
    private static final String [] sceneslist ={"Outdoor sports","Eating outside", "Sensitive people"};

    private EditText nameET;
    private EditText bdET;
    private RadioGroup genderRBG;
    private RadioButton genderRB;
    private Spinner scenesSP;
    private ArrayAdapter<String> sadapter = null;

    private String scenes;

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
        //save the usr profile
        SharedPreferences sharedPreferences = getSharedPreferences("PROFILES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usrname",nameET.getText().toString().trim());
        editor.putString("userbirth",bdET.getText().toString().trim());
        int radiobuttonid = genderRBG.getCheckedRadioButtonId();
        genderRB = (RadioButton) findViewById(radiobuttonid);
        editor.putString("usrgender",genderRB.getText().toString().trim());
        editor.putString("usrscenes",scenes);
        editor.putBoolean("used",true);
        editor.apply();

        //turn to MainActivity
        startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        ProfileActivity.this.finish();
    }

    public void rbclick(View v)
    {
        int radiobuttonid = genderRBG.getCheckedRadioButtonId();
        genderRB = (RadioButton) findViewById(radiobuttonid);
    }
}
