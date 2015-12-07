package ivy.sokken.multiposts;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Field;

@SuppressWarnings("ALL")
public class WebActivity extends Activity implements Variable {

    // WebView
    private WebView[] webView = new WebView[4];
    // 下部ボタン
    private ImageView[] iv = new ImageView[4];

    // カスタムView
    private FrameLayout customViewContainer;
    private View videoCustomView;
    private int showFlag = -1;

    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageForAfterLollipop;
    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 画面自動回転
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        // レイアウトを配置
        setContentView(R.layout.webbrows);

        context = this.context;

        Intent intent = getIntent();
        // userとpassを受け取る
        for (int i = 0; i < USER.length; i++) {
            USER_ACCOUNT[i][0] = intent.getStringExtra(USER[i]);
            USER_ACCOUNT[i][1] = intent.getStringExtra(PASS[i]);
        }

        // View取得
        findViewById2();

        // 下部ボタン設定
        bt_footer();

        // WebView 初期設定
        wbFirstSet();

    }


    // 初期　View取得まとめ
    void findViewById2() {
        customViewContainer = (FrameLayout) findViewById(R.id.fl_web_brows);

        // WebView取得
        webView[TWITTER] = (WebView) findViewById(R.id.wv_webbrows_twitter);
        webView[FACEBOOK] = (WebView) findViewById(R.id.wv_webbrows_facebook);
        webView[MIXI] = (WebView) findViewById(R.id.wv_webbrows_mixi);
        webView[GOOGLEPLUS] = (WebView) findViewById(R.id.wv_webbrows_googleplus);

        // 下部４ボタンのImageView取得
        iv[TWITTER] = (ImageView) findViewById(R.id.iv_webbrows_twitter);
        iv[FACEBOOK] = (ImageView) findViewById(R.id.iv_webbrows_facebook);
        iv[MIXI] = (ImageView) findViewById(R.id.iv_webbrows_mixi);
        iv[GOOGLEPLUS] = (ImageView) findViewById(R.id.iv_webbrows_googleplus);

    }

    // ホームボタンのクリックリスナー
    void bt_footer() {

        // ホームボタンのクリックリスナー
        ((ImageView) findViewById(R.id.iv_webbrows_home)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ホームボタンがクリックされたら、全てのWebViewのロードを停止する。
                for (WebView view : webView) {
                    //　ロード停止
                    view.stopLoading();
                }

                // メイン画面遷移
                Intent intent = new Intent(context, MainActivity.class);
                // 遷移する際、現在のアクティビティを破棄
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // 遷移先をホーム画面にセット。
                intent.putExtra("flg", 1);
                // アクティビティ実行
                startActivity(intent);
            }
        });

        // ロードが完了していない又は、ログインしていないSNSはボタンの色を変更
        for (ImageView iv : this.iv) {
            // ボタンをうすい灰色表示にする(非活性化ボタン風)
            iv.setColorFilter(Color.GRAY & 0xd0FFFFFF);
        }

    }


    void wbFirstSet() {
        // SNSリログ用にクッキーをすべて削除
        //CookieManager.getInstance().removeAllCookie();

        for (final WebView view : webView) {

            // ページバックキー処理
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // 現在表示されているWebViewを指定
                    if (keyCode == KeyEvent.KEYCODE_BACK && view.getVisibility() == View.VISIBLE && view.canGoBack()) {
                        // ページバック
                        view.goBack();
                        return true;
                    }
                    return false;
                }
            });

            // SNSリログ用にキャッシュ等削除
            clear(view);
            // WebViewでURLを開く設定(Android標準ブラウザを無効)
            view.setWebViewClient(new ViewClient());
            // 動画再生用処理
            view.setWebChromeClient(new ViewChromeClient());

            // WebView設定
            WebSettings ws = view.getSettings();

            // ファイルアクセス許可
            ws.setAllowFileAccess(true);
            // フォームデータ保存設定
            ws.setSaveFormData(false);
            // パスワードの保存設定
            ws.setSavePassword(false);
            // javascript設定
            ws.setJavaScriptEnabled(true);
            // plugin設定
            ws.setPluginState(WebSettings.PluginState.ON);
            // キャッシュ設定
            ws.setAppCacheEnabled(true);
            // キャッシュモード（更新があれば再取得に設定）
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
            // キャッシュサイズ（byte）
            ws.setAppCacheMaxSize(32 * 1024 * 1024);
            // キャッシュ格納場所のパス
            ws.setAppCachePath("/data/data/" + getPackageName() + "/cache");

            //zoom control
            ws.setBuiltInZoomControls(true);

            try {
                //マルチタッチを有効にしたまま、zoom controlボタンを消す
                Field nameField = ws.getClass().getDeclaredField("mBuiltInZoomControls");
                nameField.setAccessible(true);
                nameField.set(ws, false);
            } catch (Exception e) {
                e.printStackTrace();
                ws.setBuiltInZoomControls(false);
            }

        }


        /*
        最初に表示するWebViewとロードするページの設定
        */
        if (USER_ACCOUNT[TWITTER][0].length() > 0) {
            showFlag = TWITTER;
            webView[0].loadUrl(TWITTER_LOGIN_URL);
        }
        if (USER_ACCOUNT[FACEBOOK][0].length() > 0) {
            if (showFlag < 0) showFlag = FACEBOOK;
            webView[1].loadUrl(FACEBOOK_LOGIN_URL);
        }
        if (USER_ACCOUNT[MIXI][0].length() > 0) {
            if (showFlag < 0) showFlag = MIXI;
            webView[2].loadUrl(MIXI_LOGIN_URL);
        }
        if (USER_ACCOUNT[GOOGLEPLUS][0].length() > 0) {
            if (showFlag < 0) showFlag = GOOGLEPLUS;
            webView[3].loadUrl(GOOGLEPLUS_LOGIN_URL);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if ( mUploadMessage == null ){
                return;
            }
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }


    // WebViewClientを継承
    public final class ViewClient extends WebViewClient {

        // ロード上書き
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // ログアウト時の処理
            if (url.contains(TWITTER_LOGOUT_URL)
                    || url.contains(FACEBOOK_LOGOUT_URL) ) {

                // 全てのWebViewを非表示
                setVisibility(-1);
                // WebViewのロードを停止
                view.stopLoading();
                // SNSリログ用にキャッシュ等削除
                clear(view);
                // WebActivity終了
                finish();
            }

            // Falseで このままWebViewを使用
            return false;
        }

        // ロード開始時
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            // ユーザーエージェント
            String useragent = "";

            // TwitterログインURLアクセス時
            if (url.equals(TWITTER_LOGIN_URL)) {

                // ユーザーエージェントをPCブラウザに変更
                useragent = FIREFOX;
            }

            // Twitter利用時
            else if (url.contains(TWITTER_COM_URL)) {
                // ユーザーエージェントをPCブラウザに変更
                useragent = FIREFOX;
            }

            // Facebook利用時
            else if (url.contains(M_FACEBOOK_COM_URL)){

                // ユーザーエージェントをAndroidブラウザに変更
                useragent = ANDROID;

            }
            else if (url.contains(YOUTUBE_URL)) {
            }
            else {
                // ユーザーエージェントをPCブラウザに変更
                useragent = FIREFOX;
            }

            // viewにユーザーエージェントを設定
            view.getSettings().setUserAgentString(useragent);
        }


        //ロード完了時
        @Override
        public void onPageFinished(WebView view , String url){


            // Twitterログイン画面
            if (url.equals("https://mobile.twitter.com/session/new")) {

                // オートログイン処理
                loadJS(view, "twitter");

            }

            // Twitterのログイン画面以外は可視化
            else if (url.contains(TWITTER_URL)) {

                // WebViewを可視化
                setVisibility(showFlag);
                ButtonEnable(TWITTER);


            }

            // Facebookログイン画面
            else if (url.contains(FACEBOOK_LOGIN_URL) && view.getTitle() != null && view.getTitle().equals("Facebookへようこそ")) {

                // オートログイン処理
                loadJS(view, "facebook");
            }

            // Facebookのログイン画面以外は可視化
            else if (url.contains(FACEBOOK_URL) && view.getTitle() != null  && !view.getTitle().equals("Facebookへようこそ")) {

                // WebViewを可視化
                setVisibility(showFlag);
                ButtonEnable(FACEBOOK);

            }

        }
    }

    //WebViewChromeClientを継承
    private class ViewChromeClient extends WebChromeClient{

        private static final String TAG = "ViewChromeClient";

        // フルスクリーン処理(Playerの取出し＆再配置)
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {

            if (videoCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            final FrameLayout frame = ((FrameLayout) view);

            final View v1 = frame.getChildAt(0);
            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));

            videoCustomView = view;
            videoCustomView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onHideCustomView();
                    Log.d("mVideoView.KeyListener", "ok");
                    return true;
                }
            });

            //customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.setBackgroundColor(Color.BLACK);
            customViewContainer.bringToFront();
            webView[0].setVisibility(View.GONE);

            customViewContainer.addView(videoCustomView);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();

            customViewContainer.removeView(videoCustomView);
            videoCustomView = null;
            //customViewContainer.setVisibility(View.GONE);
            webView[0].setVisibility(View.VISIBLE);

        }

        /**
         * JSのアラートをトースト表示にする
         */
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("onJsAlert", url);
            Log.d("onJsAlert", message);
            Log.d("onJsAlert", result.toString());
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            //return super.onJsAlert(view, url, message, result);
            return true;
        }

        @Override
        public boolean onConsoleMessage(@NonNull ConsoleMessage cm) {
            Log.d(TAG, cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
            return true;
        }

        /**
         * input type="file" 対応 (androidOS 5.0 以上)
         */
        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams)
        {
            Log.d(TAG, "onShowFileChooser started.");
            Toast.makeText(context, "ファイルを選択して下さい", Toast.LENGTH_LONG).show();
            super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            if( mUploadMessageForAfterLollipop != null) {
                mUploadMessageForAfterLollipop.onReceiveValue(null);
                mUploadMessageForAfterLollipop = null;
            }
            mUploadMessageForAfterLollipop = filePathCallback;
            Intent i = fileChooserParams.createIntent();
            try {
                startActivityForResult(i, FILE_CHOOSER_RESULT_CODE);
            } catch (ActivityNotFoundException e) {
                mUploadMessageForAfterLollipop = null;
                return false;
            }
            return true;
        }

        // input type="file" 対応 (androidOS 4.1)
        // 参考URL : http://qiita.com/masahide318/items/06af79ed8081ef725d76
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            Log.d(TAG, "openFileChooser start");
            Log.d(TAG, "acceptType : " + acceptType);
            Log.d(TAG, "capture : " + capture);

            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("file/*");
            startActivityForResult(Intent.createChooser(intent, "選択"), FILE_CHOOSER_RESULT_CODE);
        }

        // input type="file" 対応 (androidOS 3.0 以上)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, "");
        }

        // input type="file" 対応 (androidOS 3.0 未満)
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "", "");
        }

    }


    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
    */

    // 画面下のボタン
    private class FooterButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // 可視化するWebViewの番号を選択する変数
            int next = -1;

            // クリック時の処理
            switch (v.getId()) {

                // Twitter
                case R.id.iv_webbrows_twitter:

                    next = TWITTER;
                    break;

                // Facebook
                case R.id.iv_webbrows_facebook:

                    next = FACEBOOK;
                    break;

                // mixi
                case R.id.iv_webbrows_mixi:

                    next = MIXI;
                    break;

                // Google+
                case R.id.iv_webbrows_googleplus:

                    next = GOOGLEPLUS;
                    break;

                default:
                    break;
            }

            // WebView可視化
            setVisibility(next);
            showFlag = next;

        }
    }


    // WebView(SNS)切り替え用
    void setVisibility(int id) {

        for (WebView view : webView) {
            // 詰めて消す
            view.setVisibility(View.GONE);
        }

        // 受け取った引数で可視化するWebViewを決定
        if (id != -1) {
            // 可視化
            webView[id].setVisibility(View.VISIBLE);
        }
    }

    void loadJS(WebView view, String sns) {

        StringBuilder sb = new StringBuilder();

        if (sns.equals("twitter")) {

            sb.append("javascript:document.getElementById(\"session[username_or_email]\").setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[TWITTER][0]);
            sb.append("\");document.getElementById(\"session[password]\").setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[TWITTER][1]);
            sb.append("\");document.getElementsByTagName(\"form\")[0].submit();");

            // Twitterオートログイン
            view.loadUrl(sb.toString());

        } else if (sns.equals("facebook")) {

            sb.append("javascript:document.getElementsByName(\"email\")[0].setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[FACEBOOK][0]);
            sb.append("\");document.getElementsByName(\"pass\")[0].setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[FACEBOOK][1]);
            sb.append("\");document.getElementsByTagName(\"form\")[0].submit();");

            // Facebookオートログイン
            view.loadUrl(sb.toString());

        }
        else if (false) {
            // mixiオートログイン

        }
        else if (false) {
            // GooglePlusオートログイン

        }
    }

    void ButtonEnable(int id) {

        // グレーフィルター解除
        iv[id].setColorFilter(0);

        // クリックリスナー設定
        iv[id].setOnClickListener(new FooterButton());
    }

    void clear(WebView view) {

        // FacebookのWebViewはクリアしない
        if(view == webView[1]) return;

        // キャッシュ削除
        view.clearCache(true);
        // フォーム入力データ削除
        view.clearFormData();
        // 閲覧履歴削除
        view.clearHistory();

    }

}
