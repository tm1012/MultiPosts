package ivy.sokken.multiposts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.xwalk.core.XWalkView;

public class MainActivity extends Activity implements Constants {

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

            // バグ回避用（先にメモリ上にXWalkViewクラスを読み込ませる）
            (new XWalkView(this, this)).load("", null);
        }

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_top_next:
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

                Intent intent = new Intent(this, WebActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                for (int i = 0; i < rg.length; i++) {
                    rb[i] = (RadioButton) findViewById(rg[i].getCheckedRadioButtonId());
                    intent.putExtra(USER[i], rb[i].getText());
                    intent.putExtra(PASS[i], rb[i].getHint());
                }

                startActivity(intent);
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
