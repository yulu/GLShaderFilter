package com.littlecheesecake.glshaderfilter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.littlecheesecake.glshaderfilter.api.ShaderCamera;
import com.littlecheesecake.glshaderfilter.api.ShaderFilterType;
import com.littlecheesecake.glshaderfilter.gl.FilterRenderer;


public class MainActivity extends Activity {
    private ShaderCamera mSC;
    private FilterRenderer mRenderer;

    //view
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRenderer = (FilterRenderer)findViewById(R.id.cameraview);
        mSC = ShaderCamera.getInstance(mRenderer);

        button1 = (Button)findViewById(R.id.filter_1);
        button2 = (Button)findViewById(R.id.filter_2);
        button3 = (Button)findViewById(R.id.filter_3);
        button4 = (Button)findViewById(R.id.filter_4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.setFilter(ShaderFilterType.FILTER_METAL);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.setFilter(ShaderFilterType.FILTER_OLDTIME);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.setFilter(ShaderFilterType.FILTER_ICE);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSC.setFilter(ShaderFilterType.FILTER_NONE);
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        mSC.stopCamera();
        mRenderer.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRenderer.onResume();
        mSC.restartCamera(mRenderer);
    }


    @Override
    public void onStart() {
        super.onStart();
        mSC.registerCamera(mRenderer);
    }
}
