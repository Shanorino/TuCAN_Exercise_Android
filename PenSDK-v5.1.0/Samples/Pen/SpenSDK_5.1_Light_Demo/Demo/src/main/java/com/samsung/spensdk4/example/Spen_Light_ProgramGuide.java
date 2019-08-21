package com.samsung.spensdk4.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.samsung.android.sdk.pen.pg.example1_1.PenSample1_1_HelloPen;
import com.samsung.android.sdk.pen.pg.example1_2.PenSample1_2_PenSetting;
import com.samsung.android.sdk.pen.pg.example1_3.PenSample1_3_EraserSetting;
import com.samsung.android.sdk.pen.pg.example1_4.PenSample1_4_UndoRedo;
import com.samsung.android.sdk.pen.pg.example1_5.PenSample1_5_Background;
import com.samsung.android.sdk.pen.pg.example1_6.PenSample1_6_Capture;
import com.samsung.android.sdk.pen.pg.example2_1.PenSample2_1_StrokeObject;
import com.samsung.android.sdk.pen.pg.example2_2.PenSample2_2_SaveFile;
import com.samsung.android.sdk.pen.pg.example2_3.PenSample2_3_LoadFile;
import com.samsung.android.sdk.pen.pg.example2_4.PenSample2_4_AddPage;
import com.samsung.android.sdk.pen.pg.example3_1.PenSample3_1_SelectionSetting;
import com.samsung.android.sdk.pen.pg.example3_2.PenSample3_2_ChangeObjectOrder;
import com.samsung.android.sdk.pen.pg.example4_1.PenSample4_1_SimpleView;
import com.samsung.android.sdk.pen.pg.example4_2.PenSample4_2_OnlyPen;
import com.samsung.spensdk4light.example.R;

public class Spen_Light_ProgramGuide extends Activity {

    private ListAdapter mListAdapter = null;
    private ListView mListView = null;

    // The item of list
    private static final int SPEN_HELLOPEN = 0;
    private static final int SPEN_PENSETTING = 1;
    private static final int SPEN_ERASERSETTING = 2;
    private static final int SPEN_UNDOREDO = 3;
    private static final int SPEN_BACKGROUND = 4;
    private static final int SPEN_CAPTURE = 5;
    private static final int SPEN_STROKEOBJECT = 6;
    private static final int SPEN_SAVEFILE = 7;
    private static final int SPEN_LOADFILE = 8;
    private static final int SPEN_ADDPAGE = 9;
    private static final int SPEN_SELECTIONSETTING = 10;
    private static final int SPEN_MOVEOBJECT = 11;
    private static final int SPEN_SIMPLEVIEW = 12;
    private static final int SPEN_ONLYPEN = 13;
    private static final int TOTAL_LIST_NUM = 14;

    private final String EXAMPLE_NAMES[] = {
            "1.1 Hello Pen",
            "1.2 Pen Setting",
            "1.3 Eraser Setting",
            "1.4 Undo & Redo",
            "1.5 Background",
            "1.6 Capture",
            "2.1 Stroke Object",
            "2.2 Save File",
            "2.3 Load File",
            "2.4 Add Page",
            "3.1 Selection Setting",
            "3.2 Change Object Order",
            "4.1 Simple View",
            "4.2 Only Pen",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spensdk_demo);

        createUI();
    }

    private void createUI() {

        TextView textTitle = (TextView) findViewById(R.id.title);
        textTitle.setText("Spen Light Program Guide");

        mListAdapter = new ListAdapter(this);
        mListView = (ListView) findViewById(R.id.demo_list);
        mListView.setAdapter(mListAdapter);

        mListView.setItemsCanFocus(false);
        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // S Pen SDK Demo programs
                if (position == SPEN_HELLOPEN) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample1_1_HelloPen.class);
                    startActivity(intent);
                } else if (position == SPEN_PENSETTING) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample1_2_PenSetting.class);
                    startActivity(intent);
                } else if (position == SPEN_ERASERSETTING) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample1_3_EraserSetting.class);
                    startActivity(intent);
                } else if (position == SPEN_UNDOREDO) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample1_4_UndoRedo.class);
                    startActivity(intent);
                } else if (position == SPEN_BACKGROUND) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample1_5_Background.class);
                    startActivity(intent);
                } else if (position == SPEN_CAPTURE) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample1_6_Capture.class);
                    startActivity(intent);
                } else if (position == SPEN_STROKEOBJECT) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample2_1_StrokeObject.class);
                    startActivity(intent);
                } else if (position == SPEN_SAVEFILE) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample2_2_SaveFile.class);
                    startActivity(intent);
                } else if (position == SPEN_LOADFILE) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample2_3_LoadFile.class);
                    startActivity(intent);
                } else if (position == SPEN_ADDPAGE) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample2_4_AddPage.class);
                    startActivity(intent);
                } else if (position == SPEN_SELECTIONSETTING) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample3_1_SelectionSetting.class);
                    startActivity(intent);
                } else if (position == SPEN_MOVEOBJECT) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample3_2_ChangeObjectOrder.class);
                    startActivity(intent);
                } else if (position == SPEN_SIMPLEVIEW) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample4_1_SimpleView.class);
                    startActivity(intent);
                } else if (position == SPEN_ONLYPEN) {
                    Intent intent = new Intent(Spen_Light_ProgramGuide.this, PenSample4_2_OnlyPen.class);
                    startActivity(intent);
                }
            }
        });
    }

    // =========================================
    // List Adapter : S Pen SDK Demo Programs
    // =========================================
    public class ListAdapter extends BaseAdapter {

        public ListAdapter(Context context) {
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                final LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.spensdk_demolist_item, parent, false);
            }
            // UI Item
            TextView tvListItemText = (TextView) convertView.findViewById(R.id.listitemText);
            tvListItemText.setTextColor(0xFFFFFFFF);

            // ==================================
            // basic data display
            // ==================================
            if (position < TOTAL_LIST_NUM) {
                tvListItemText.setText(EXAMPLE_NAMES[position]);
            }

            return convertView;
        }

        public void updateDisplay() {
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return TOTAL_LIST_NUM;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
