package com.rybarczykzsl.spacerowicz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.widget.ShareActionProvider;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

public class IWillDoWhatIMustActivity extends AppCompatActivity {

    private int selectedWalkId = 0;
    private ShareActionProvider shareActionProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_will_do_what_i_must);
        ArrayAdapter<Walk> listAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, Walk.walks);
        ListView listWalks = (ListView) findViewById(R.id.list_walks);
        listWalks.setAdapter(listAdapter);

        AdapterView.OnItemClickListener itemClickListener =
                new AdapterView.OnItemClickListener(){
                    public void onItemClick(AdapterView<?> listDrinks,
                                            View itemView,
                                            int position,
                                            long id) {
                        setSelectedWalkId(position);
                        displayWalk();
                    }
                };
        listWalks.setOnItemClickListener(itemClickListener);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText editText = (EditText) findViewById(R.id.name_edit_text);
        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
//                setShareActionIntent();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        displayWalk();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                setShareActionIntent();
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sharing, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        setShareActionIntent();

        return super.onCreateOptionsMenu(menu);
    }
    public void setShareActionIntent() {

        Walk selectedWalk = Walk.walks[selectedWalkId];
        String messageStr = "";
        CheckBox cb = (CheckBox) findViewById(R.id.dont_send_name_checkbox);
        EditText editText = (EditText) findViewById(R.id.name_edit_text);
        if(!cb.isChecked()){
            messageStr+="Cześć "+editText.getText().toString()+"!\n";
        }
        messageStr+="Pobierz aplikację Spacerowicz i wypróbuj "+selectedWalk.getName().toString()+":\n"+selectedWalk.getDesc().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, messageStr);
//        shareActionProvider.setShareIntent(intent);
        startActivity(intent);
    }


    private void setSelectedWalkId(int walkId){
        selectedWalkId = walkId;
    }
    private void displayWalk(){
        TextView tv = (TextView) findViewById(R.id.text_view_selected_walk);
        tv.setText("Wybrano: "+Walk.walks[selectedWalkId].getName());
    }
}