package ivy.sokken.multiposts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity implements Variable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 縦画面固定
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent intent = getIntent();
        if (intent.getIntExtra("flg", -1) == 1) {
            setContentView(R.layout.main);
        } else {
            setContentView(R.layout.top);
        }

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_top_next:
                setContentView(R.layout.main);
                break;

            case R.id.iv_main_sns:
                setContentView(R.layout.sns_account);
                break;

            case R.id.iv_sns_account_footer_next:
                RadioGroup[] rg = new RadioGroup[4];
                rg[TWITTER] = (RadioGroup) findViewById(R.id.rg_sns_account_twitter);
                rg[FACEBOOK] = (RadioGroup) findViewById(R.id.rg_sns_account_facebook);
                rg[MIXI] = (RadioGroup) findViewById(R.id.rg_sns_account_mixi);
                rg[GOOGLEPLUS] = (RadioGroup) findViewById(R.id.rg_sns_account_googleplus);
                RadioButton[] rb = new RadioButton[4];

                Intent intent = new Intent(this, TestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                /*

                for (int i = 0; i < rg.length; i++) {
                    rb[i] = (RadioButton) findViewById(rg[i].getCheckedRadioButtonId());
                    intent.putExtra(USER[i], rb[i].getText());
                    intent.putExtra(PASS[i], rb[i].getHint());
                }
                */
                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(Menu.NONE, 0, 0, "メニュー1");
        menu.add(Menu.NONE, 1, 1, "メニュー2");
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
