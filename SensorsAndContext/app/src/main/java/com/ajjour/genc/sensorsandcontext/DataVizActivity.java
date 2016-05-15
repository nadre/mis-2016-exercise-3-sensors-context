package com.ajjour.genc.sensorsandcontext;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DataVizActivity extends AppCompatActivity {

    private DataVizView dataVizView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_viz);

        dataVizView = (DataVizView) findViewById(R.id.data_viz_canvas);
    }

    public void clearCanvas(){
        dataVizView.clearCanvas();
    }

}
