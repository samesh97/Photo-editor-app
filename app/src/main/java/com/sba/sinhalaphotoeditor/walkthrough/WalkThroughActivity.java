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

    ArrayList<Drawable> topImages = new ArrayList<Drawable>();

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();


    Button next,previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_through);


        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);



        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage1));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage2));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage2));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage2));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage2));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage3));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage2));
        topImages.add(getResources().getDrawable(R.drawable.walkthroughimage4));




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

        titles.add(getResources().getString(R.string.title1));
        description.add(getResources().getString(R.string.desc1));


        titles.add(getResources().getString(R.string.title2));
        description.add(getResources().getString(R.string.desc2));


        titles.add(getResources().getString(R.string.title3));
        description.add(getResources().getString(R.string.desc3));

        titles.add(getResources().getString(R.string.title4));
        description.add(getResources().getString(R.string.desc4));


        titles.add(getResources().getString(R.string.title5));
        description.add(getResources().getString(R.string.desc5));

        titles.add(getResources().getString(R.string.title6));
        description.add(getResources().getString(R.string.desc6));


        titles.add(getResources().getString(R.string.title7));
        description.add(getResources().getString(R.string.desc7));


        titles.add(getResources().getString(R.string.title8));
        description.add(getResources().getString(R.string.desc8));

        previous.setVisibility(View.GONE);
        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),image1,image2,image3,titles,description,topImages);
        viewPager.setAdapter(viewPagerAdapter);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(next.getText().equals(getResources().getString(R.string.finish_text)))
                {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("com.sba.photoeditor", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isWalkThroughNeeded",true);
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
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
                    next.setText(getResources().getString(R.string.finish_text));
                }
                else
                {
                    next.setText(getResources().getString(R.string.next_text));
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
            public void onPageScrollStateChanged(int state)
            {

            }
        });




    }
}
