package com.sba.sinhalaphotoeditor.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sba.sinhalaphotoeditor.R;
import com.sba.sinhalaphotoeditor.adapters.StickerAdapter;
import com.sba.sinhalaphotoeditor.model.FirebaseSticker;
import com.sba.sinhalaphotoeditor.model.FirebaseStickerCategory;

import java.util.ArrayList;

public class StickerFragment extends Fragment
{
    private FirebaseStickerCategory category;
    private ArrayList<String> imageList = new ArrayList<>();
    private static final String FIREBASE_STICKERS_IMAGE_MAIN_PATH = "Stickers";
    private static final String FIREBASE_STICKERS_IMAGE_SUB_PATH = "Images";

    public StickerFragment(FirebaseStickerCategory category)
    {
        this.category = category;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.sticker_fragment_layout,null);

        RecyclerView rv_stickers = view.findViewById(R.id.rv_stickers);
        StickerAdapter adapter = new StickerAdapter(getContext(),imageList);
        GridLayoutManager manager = new GridLayoutManager(getContext(),5);
        setRecyclerView(rv_stickers,adapter,manager);
        rv_stickers.setAdapter(adapter);
        rv_stickers.setLayoutManager(manager);

        return  view;
    }

    private void setRecyclerView(RecyclerView rv_stickers, final StickerAdapter adapter, GridLayoutManager manager)
    {
        imageList.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FIREBASE_STICKERS_IMAGE_MAIN_PATH).child(FIREBASE_STICKERS_IMAGE_SUB_PATH);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot dss : dataSnapshot.getChildren())
                {
                    FirebaseSticker sticker = dss.getValue(FirebaseSticker.class);
                    if(sticker != null && sticker.getId() != null && sticker.getId().equals(category.getId()) && sticker.getPath() != null)
                    {
                        imageList.add(sticker.getPath());
                        adapter.notifyDataSetChanged();
                        Log.d("hello",sticker.getPath());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }
}
