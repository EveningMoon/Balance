package com.emoon.balance.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.emoon.balance.Etc.Constants;
import com.emoon.balance.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class SettingFragment extends Fragment {

    private View view;
    private ViewGroup resetBtn;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        init();
        addListeners();
    }

    private void init() {
        resetBtn = (ViewGroup) view.findViewById(R.id.resetBtn);
    }

    private void addListeners(){
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "reset click", Toast.LENGTH_SHORT).show();
                exportDB();
            }
        });
    }

    private void exportDB() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                if(createDirectory()){ Log.d("FILE", "can write file");
                    String currentDBPath = "//data//" + "com.emoon.balance" + "//files//" + Constants.REALM_NAME;
                    String backupDBPath = "Balance/" + Constants.REALM_NAME;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    File exportRealmFile = new File(Environment.getExternalStorageDirectory().toString() + "/Balance/" + Constants.REALM_NAME);
                    email(exportRealmFile);
                }else{
                    Log.d("FILE","cannot write file");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean createDirectory(){
        File directory = new File(Environment.getExternalStorageDirectory().toString() + "/Balance");

        //If file doesnt exist
        if(!directory.exists()){
            return directory.mkdirs();
        }else{
            return true;
        }
    }

    public void email(File file) {
        // init email intent and add file as attachment
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, "YOUR MAIL");
        intent.putExtra(Intent.EXTRA_SUBJECT, "YOUR SUBJECT");
        intent.putExtra(Intent.EXTRA_TEXT, "YOUR TEXT");
        Uri u = Uri.fromFile(file);
        Log.d("REALM", " u : "+u.getPath());
        intent.putExtra(Intent.EXTRA_STREAM, u);

        // start email intent
        startActivity(Intent.createChooser(intent, "YOUR CHOOSER TITLE"));
    }
}
