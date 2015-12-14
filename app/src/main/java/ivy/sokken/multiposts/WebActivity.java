package ivy.sokken.multiposts;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceResponse;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

@SuppressWarnings("ALL")
public class WebActivity extends Activity implements Constants {

    // WebView
    private XWalkView[] xWalkViews = new XWalkView[4];
    // 下部ボタン
    private ImageView[] iv = new ImageView[4];

    // カスタムView
    private FrameLayout customViewContainer;
    private View videoCustomView;
    private int showFlag = -1;

    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private ValueCallback<Uri> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 画面自動回転
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        // レイアウトを配置
        setContentView(R.layout.webbrows);

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

        // XWalkiew 初期設定
        xwbFirstSet();

    }

    @Override
    protected void onPause() {
        super.onPause();
        for (XWalkView xwv : xWalkViews) {
            if (xwv != null) {
                xwv.pauseTimers();
                xwv.onHide();
            }
        }
        onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        for (XWalkView xwv : xWalkViews) {
            if (xwv != null) {
                xwv.resumeTimers();
                xwv.onShow();
            }
        }
        onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (XWalkView xwv : xWalkViews) {
            if (xwv != null) {
                xwv.onDestroy();
            }
        }
    }

    // 初期　View取得まとめ
    void findViewById2() {
        customViewContainer = (FrameLayout) findViewById(R.id.fl_web_brows);

        // XWalkView取得
        xWalkViews[TWITTER]= (XWalkView) findViewById(R.id.xwv_webbrows_twitter);
        xWalkViews[FACEBOOK] = (XWalkView) findViewById(R.id.xwv_webbrows_facebook);
        xWalkViews[MIXI] = (XWalkView) findViewById(R.id.xwv_webbrows_mixi);
        xWalkViews[GOOGLEPLUS] = (XWalkView) findViewById(R.id.xwv_webbrows_googleplus);

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

                // ホームボタンがクリックされたら、全てのXWalkViewのロードを停止する。
                for (XWalkView xview : xWalkViews) {
                    //　ロード停止
                    xview.stopLoading();
                }

                // メイン画面遷移
                Intent intent = new Intent(WebActivity.this, MainActivity.class);
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

    //
    void xwbFirstSet() {

        for (final XWalkView xview : xWalkViews) {

            // ページバックキー処理
            xview.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    XWalkView xv = (XWalkView) v;
                    // 現在表示されているWebViewを指定
                    if (keyCode == KeyEvent.KEYCODE_BACK && xv.getVisibility() == View.VISIBLE && xv.getNavigationHistory().canGoBack()) {
                        // ページバック
                        return false;
                    } else {
                        // フック
                        return true;
                    }
                }
            });

            // SNSリログ用にキャッシュ等削除
            clear(xview);
            //
            xview.setResourceClient(new ResourceClient(xview));
            //
            xview.setUIClient(new UIClient(xview));

        }


        /*
        最初に表示するWebViewとロードするページの設定
        */
        if (USER_ACCOUNT[TWITTER][0].length() > 0) {
            showFlag = TWITTER;
            xWalkViews[TWITTER].load(TWITTER_LOGIN_URL, null);
            setZOrderOnTop(TWITTER);
        }
        if (USER_ACCOUNT[FACEBOOK][0].length() > 0) {
            if (showFlag < 0) showFlag = FACEBOOK;
            //xWalkViews[FACEBOOK].load(FACEBOOK_LOGIN_URL, null);
        }
        if (USER_ACCOUNT[MIXI][0].length() > 0) {
            if (showFlag < 0) showFlag = MIXI;
            //xWalkViews[MIXI].load(MIXI_LOGIN_URL, null);
        }
        if (USER_ACCOUNT[GOOGLEPLUS][0].length() > 0) {
            if (showFlag < 0) showFlag = GOOGLEPLUS;
            //xWalkViews[GOOGLEPLUS].load(GOOGLEPLUS_LOGIN_URL, null);
        }


    }

    // startActivityForResultから呼び出したIntentの戻り
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {

        // 送ったリザルトコードの引数と元のリザルトコードが一致すれば処理続行
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {

            // msgがなければ
            if ( mUploadMessage == null){
                return;
            }

            // ファイルのUri
            Uri result;
            if (intent == null || resultCode != RESULT_OK) {
                result = null;
            }
            else {
                // Uri取得
                result = intent.getData();

                // ファイルパス取得用カーソル
                Cursor c = getContentResolver().query(result, null, null, null, null);

                // カーソルがなければスキーマを変換しない
                if(c != null) {
                    // カーソルを最初に戻す
                    c.moveToFirst();
                    //　列指定
                    String columns = MediaStore.MediaColumns.DATA;
                    // 列番号取得
                    int index = c.getColumnIndex(columns);

                    // 列が見つかったら
                    if( index > 0)
                        // ファイルパス取得
                        result = Uri.parse(c.getString(index));

                    c.close();
                }
            }

            // 返す
            mUploadMessage.onReceiveValue(result);
            Log.d("onActivityResult", result.toString());
            mUploadMessage = null;
        }
    }

    //
    class UIClient extends XWalkUIClient {
        public UIClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        // ファイル選択画面呼び出し
        public void openFileChooser(XWalkView view,
                                    ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            super.openFileChooser(view, uploadFile, acceptType, capture);

            mUploadMessage = uploadFile;

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            // 画像
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "選択"), FILE_CHOOSER_RESULT_CODE);

        }
    }

    //
    class ResourceClient extends XWalkResourceClient {

        //　コンストラクタ
        public ResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
            Log.d("MyXWalkResourceClient", "Load Failed:" + description);
            //super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        // ロード上書き
        @Override
        public boolean shouldOverrideUrlLoading(XWalkView xview, String url) {
            //super.shouldOverrideUrlLoading(xview, url);

            // ログアウト時の処理
            if (url.contains(TWITTER_LOGOUT_URL)
                    || url.contains(FACEBOOK_LOGOUT_URL) ) {

                // 全てのXWalkViewを非表示
                setZOrderOnTop(-1);
                // XWalkViewのロードを停止
                xview.stopLoading();
                // SNSリログ用にキャッシュ等削除
                clear(xview);
                // WebActivity終了
                finish();
            }

            // Falseで このままXWalkViewを使用
            return false;
        }

        // ロード開始時
        @Override
        public void onLoadStarted(XWalkView xview, String url) {
            //super.onLoadStarted(xview, url);

            // ユーザーエージェント
            String useragent = "";

            switch (url) {
                case M_FACEBOOK_COM_URL:   // Facebook利用時
                case MIXI_URL:              // mixi利用時
                case GOOGLEPLUS_URL:       // Google+利用時
                    useragent = ANDROID;
                    break;
                case TWITTER_LOGIN_URL:     // TwitterログインURLアクセス時
                case TWITTER_COM_URL:       // Twitter利用時
                case YOUTUBE_URL:
                default:
                    useragent = FIREFOX;
                    break;

            }

            Log.d("aaa onLoadStarted", url);
            // XWalkViewにユーザーエージェントを設定
            Log.d("aaa useragent", useragent);
            xview.setUserAgentString(useragent);
        }


        //ロード完了時
        @Override
        public void onLoadFinished(XWalkView xview , String url) {
            //super.onLoadFinished(xview, url);


            // Twitterログイン画面
            if (url.equals("https://mobile.twitter.com/session/new")) {

                // オートログイン処理
                //loadJS(xview, "twitter");

            }

            // Twitterのログイン画面以外は可視化
            else if (url.contains(TWITTER_URL)) {

                // XWalkViewを可視化
                setZOrderOnTop(showFlag);
                ButtonEnable(TWITTER);


            }

            // Facebookログイン画面
            else if (url.contains(FACEBOOK_LOGIN_URL) && xview.getTitle() != null && xview.getTitle().equals("Facebookへようこそ")) {

                // オートログイン処理
                loadJS(xview, "facebook");
            }

            // Facebookのログイン画面以外は可視化
            else if (url.contains(FACEBOOK_URL) && xview.getTitle() != null  && !xview.getTitle().equals("Facebookへようこそ")) {

                // XWalkViewを可視化
                setZOrderOnTop(showFlag);
                ButtonEnable(FACEBOOK);

            }

            // Google+ログイン画面
            else if (url.contains(GOOGLEPLUS_LOGIN_URL) && xview.getTitle() != null && xview.getTitle().equals("ログイン - Google アカウント")) {

                // オートログイン処理
                loadJS(xview, "googleplus");
            }

            // Google+のログイン画面以外は可視化
            else if (url.contains(GOOGLEPLUS_URL) && xview.getTitle() != null  && !xview.getTitle().equals("ログイン - Google アカウント")) {

                // XWalkViewを可視化
                setZOrderOnTop(showFlag);
                ButtonEnable(GOOGLEPLUS);

            }
            Log.d("aaa onLoadFinished", url);

        }

        @Override
        public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
            /*
            try {

                URL u = new URL(url);
                // HTTPコネクション取得
                HttpURLConnection con = (HttpURLConnection) u.openConnection(Proxy.NO_PROXY);

                // User-agentリクエストヘッダを設定
                //con.setRequestProperty("User-agent", USERAGENT);

                return new WebResourceResponse(null, null, con.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            return super.shouldInterceptLoadRequest(view, url);
        }
    }

    // 下部アカウントアイコンボタンのクリックリスナー
    private class FooterButton implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            // 可視化するXWalkViewの番号を選択する変数
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

            // XWalkView可視化
            //setZOrderOnTop(next);
            showFlag = next;

        }

    }

    // XWalkView(SNS)切り替え用 Visibility変更
    void setZOrderOnTop(int id) {

        for (XWalkView xview : xWalkViews) {
            // 非表示
            xview.setZOrderOnTop(false);
        }

        // 受け取った引数で可視化するXWalkViewを決定
        if (id != -1) {
            // 表示
            Log.d("aaa visible", "ok");
            xWalkViews[id].setZOrderOnTop(true);
        }
    }

    void loadJS(XWalkView xview, String sns) {

        StringBuilder sb = new StringBuilder();

        if (sns.equals("twitter")) {

            // Twitterオートログイン
            sb.append("javascript:document.getElementById(\"session[username_or_email]\").setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[TWITTER][0]);
            sb.append("\");document.getElementById(\"session[password]\").setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[TWITTER][1]);
            sb.append("\");document.getElementsByTagName(\"form\")[0].submit();");

        } else if (sns.equals("facebook")) {

            // Facebookオートログイン
            sb.append("javascript:document.getElementsByName(\"email\")[0].setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[FACEBOOK][0]);
            sb.append("\");document.getElementsByName(\"pass\")[0].setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[FACEBOOK][1]);
            sb.append("\");document.getElementsByTagName(\"form\")[0].submit();");

        }
        else if (false) {
            // mixiオートログイン

        }
        else if (sns.equals("googleplus")) {

            // GooglePlusオートログイン
            sb.append("javascript:document.getElementById(\"Email\").setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[GOOGLEPLUS][0]);
            sb.append("\");document.getElementById(\"Passwd\").setAttribute(\"value\", \"");
            sb.append(USER_ACCOUNT[GOOGLEPLUS][1]);
            sb.append("\");document.getElementById(\"gaia_loginform\").submit();");

        }

        // javascript実行
        xview.load(sb.toString(), "");

    }

    // 下部アカウントアイコンボタン有効設定
    void ButtonEnable(int id) {

        // グレーフィルター解除
        iv[id].setColorFilter(0);

        // クリックリスナー設定
        iv[id].setOnClickListener(new FooterButton());
    }

    // キャッシュ等削除
    void clear(XWalkView xview) {

        // FacebookのXWalkViewの設定はクリアしない
        if(xview == xWalkViews[1]) return;

        // キャッシュ削除
        xview.clearCache(true);
        // ナビゲーション履歴削除
        xview.getNavigationHistory().clear();
    }

}
