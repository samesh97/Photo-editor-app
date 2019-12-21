package com.sba.sinhalaphotoeditor.walkthrough;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sba.sinhalaphotoeditor.MainActivity;
import com.sba.sinhalaphotoeditor.R;

import java.util.ArrayList;

public class WalkThroughActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    ArrayList<Drawable> image1 = new ArrayList<android.graphics.drawable.Drawable>();
    ArrayList<Drawable> image2 = new ArrayList<Drawable>();
    ArrayList<Drawable> image3 = new ArrayList<Drawable>();

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();


    Button next,previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);


        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);




        image1.add(getResources().getDrawable(R.drawable.addimage));
        image1.add(null);
        image1.add(null);
        image1.add(null);
        image1.add(null);
        image1.add(null);
        image1.add(null);

        image3.add(getResources().getDrawable(R.drawable.addfromimages));
        image3.add(null);
        image3.add(null);
        image3.add(null);
        image3.add(null);
        image3.add(null);
        image3.add(null);


        image2.add(getResources().getDrawable(R.drawable.createbitmap));
        image2.add(getResources().getDrawable(R.drawable.addtext));
        image2.add(getResources().getDrawable(R.drawable.addimage));
        image2.add(getResources().getDrawable(R.drawable.addsticker));
        image2.add(getResources().getDrawable(R.drawable.addeffect));
        image2.add(getResources().getDrawable(R.drawable.addblur));
        image2.add(getResources().getDrawable(R.drawable.addcrop));

        image1.add(getResources().getDrawable(R.drawable.undo));
        image2.add(getResources().getDrawable(R.drawable.saveimage));
        image3.add(getResources().getDrawable(R.drawable.redo));

        titles.add("පිංතූරයක් එකතු කරගන්නා ආකාර");
        description.add("මෙහි තිබෙන පලමු icon එක click කිරීමෙන් edit කිරීමට අවශ්ය photo එක ගැලරියෙන් තෝරාගත හැක.\nදෙවනි icon එකෙන් අවශ්ය පසුබිමක් සාදාගැනීමට යා හැක.\nතෙවන icon එකෙන් මීට කලින්  edit කල පින්තුරයක් ලබාගත හැක.");


        titles.add("අකුරු එකතු කරගන්නා අකාරය");
        description.add("මෙහි තිබෙන button එක භාවිතා කර ඔබට වචන type කර photo එක මතට එක් කරගත හැක.\nඑය පින්තූරයට add කරගැනීමෙන් පසුව එහි වර්ණය සහ text එකේ font එක වෙනස්කරගත හැක.\nඑම අවස්තාවේදීම text එකට shadow එකක් හෝ glow එකක් ලබාදිය හැකි අතර ලබාදුන් glow එකේ වර්ණයද වෙනස් කරගත හැක.");


        titles.add("පිංතූරයක් උඩට පිංතූරයක් එකතු කරගන්නා ආකරය");
        description.add("මෙහිදී අපට edit කරන පින්තූරය මතට තවත් photos එක් කරගැනීම සදහා ගැලරියට යා හැක.\nගැලරියෙන් photo එකක් තෝරාගෙන අවශ්\u200Dය පරිදි එහි size එක වෙනස්කරගෙන එය අවශ්\u200Dය ස්ථානයට ගෙන ගොස් උඩම ඇති හරි ලකුන click කිරීමෙන් පින්තූරයට යොදගත හැක.");

        titles.add("ස්ටිකර්ස් එකතු කරගන්නා ආකරය");
        description.add("මෙහිදී අපට පින්තුරය මතට stickers එක් කරගත හැක.\nsticker එකක් තෝරාගෙන අවශ්\u200Dය පරිදි එහි size එක වෙනස්කරගෙන එය අවශ්\u200Dය ස්ථානයට ගෙන ගොස් උඩම ඇති හරි ලකුන click කිරීමෙන් පින්තූරයට යොදගත හැක.");


        titles.add("ෆිල්ටර්ස් එකතු කරගන්නා ආකරය");
        description.add("මෙහිදී අපට photo එකට filters යොදාගත හැක.\nපහලින් එන filters click කර බලමින්  photo එකට වඩාත්ම ගැලපෙන filter එක තෝරාගත් පසු උඩම ඇති හරි ලකුන click කිරීමෙන් පින්තූරයට යොදගත හැක.");

        titles.add("පිංතූරයක් Adjust කරන ආකාරය");
        description.add("මෙහිදී අපට පින්තූරයේ වර්ණ අඩු වැඩි කරගැනීම හා blur  කරගැනීම සිදු කරගත හැක.\nමෙහි එන පලමු දර්ශකයෙන් blur effect එක යොදා එය අඩු වැඩි කරගත හැක.\nදෙවන දර්ශකයෙන් පින්තූරයේ වර්ණය අඩු වැඩි කරගත හැක.අවශ්ය පරිදි වෙනස්කම් සිදු කිරීමෙන් පසුව උඩම ඇති හරි ලකුන click කර ඒවා පින්තූරයට යොදගත හැක.");


        titles.add("පිංතූරයක් Crop කරගන්නා ආකරය");
        description.add("මෙහිදී අපට පින්තූරය crop,rotate,flip කරගත හැක.\ncrop කිරීමට අවශ්\u200Dය නම් crop කිරීමට එන සැකසුම අවශ්\u200Dය පරිදි වෙනස්කරගත යුතුයි.\nඋඩම දකුනු කෙලවරේ තිත් 3 ක් ඇති ලකුන click කිරීමෙන් පින්තූරය rotate කරගැනීමට සහ vertical හා horizontal ලෙස flip කරගැනීමට හැක.\nඅවශ්\u200Dය පරිදි වෙනස්කම් සිදු කිරීමෙන් පසු crop button එක click කර ඒවා  පින්තූරයට යොදගත හැක.");


        titles.add("undo,redo සහ save කරන ආකාරය");
        description.add("පින්තූරයක් edit කරගැනීමෙන් පසුව උඩම දකුනු කෙලවරේ ඇති හරි ලකුන click කර එම පින්තූරය share කිරීම හෝ download කිරීම කල හැක.\nතවද එතන ඇති අනෙක් buttons දෙකෙන් undo redo කරගත හැක.");

        previous.setVisibility(View.GONE);
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),image1,image2,image3,titles,description);
        viewPager.setAdapter(viewPagerAdapter);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(next.getText().equals("අවසන් කරන්න"))
                {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.photoeditor", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isWalkThroughNeeded",true);
                    editor.apply();

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1,true);

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1,true);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == (viewPager.getAdapter().getCount())-1)
                {
                    next.setText("අවසන් කරන්න");
                }
                else
                {
                    next.setText("ඉදිරියට");
                }

                if (position == 0)
                {
                    previous.setVisibility(View.INVISIBLE);
                }
                else
                {
                    previous.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }
}
