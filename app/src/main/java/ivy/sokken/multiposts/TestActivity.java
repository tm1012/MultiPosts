package ivy.sokken.multiposts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;


public class TestActivity extends Activity implements Constants {

    private XWalkView mXWalkWebView;
    private ValueCallback<Uri> mFilePathCallback;
    private final static int FILE_CHOOSER_RESULT_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mXWalkWebView = (XWalkView) findViewById(R.id.xwalkWebView);
        mXWalkWebView.setUIClient(new UIClient(mXWalkWebView));
        mXWalkWebView.setResourceClient(new ResourceClient(mXWalkWebView));
        mXWalkWebView.setUserAgentString(DOCOMO);
        mXWalkWebView.load("http://www.ugtop.com/spill.shtml", null);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == FILE_CHOOSER_RESULT_CODE)
        if (mXWalkWebView != null) {

            if (mFilePathCallback != null) {
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null
                        : intent.getData();
                if (result != null) {
                    mFilePathCallback.onReceiveValue(result);
                } else {
                    mFilePathCallback.onReceiveValue(null);
                }
            }

            mFilePathCallback = null;
        }

    }

    class UIClient extends XWalkUIClient {
        public UIClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        public void openFileChooser(XWalkView view,
                                    ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            //super.openFileChooser(view, uploadFile, acceptType, capture);

            mFilePathCallback = uploadFile;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "選択"), FILE_CHOOSER_RESULT_CODE);

        }
    }

    class ResourceClient extends XWalkResourceClient {

        public ResourceClient(XWalkView view) {
            super(view);
        }
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