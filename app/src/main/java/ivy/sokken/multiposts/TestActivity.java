package ivy.sokken.multiposts;

import android.app.Activity;
import android.os.Bundle;

import org.xwalk.core.XWalkView;


public class TestActivity extends Activity implements Constants{

    private XWalkView mXWalkWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mXWalkWebView = (XWalkView) findViewById(R.id.xwalkWebView);
        mXWalkWebView.load("https://www.google.co.jp", null);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mXWalkWebView != null) {
            mXWalkWebView.pauseTimers();
            mXWalkWebView.onHide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mXWalkWebView != null) {
            mXWalkWebView.resumeTimers();
            mXWalkWebView.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXWalkWebView != null) {
            mXWalkWebView.onDestroy();
        }
    }
}
