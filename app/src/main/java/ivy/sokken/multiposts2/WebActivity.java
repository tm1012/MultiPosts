package ivy.sokken.multiposts2;

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
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class WebActivity extends Activity implements Constants {

    // XWalkView
    private XWalkView[] xWalkViews = new XWalkView[4];
    // 下部ボタン
    private ImageView[] iv = new ImageView[4];

    private int showFlag = -1;

    private final static int FILE_CHOOSER_RESULT_CODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private FrameLayout fl;
    private XWalkCookieManager cookieManager;

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

        // XWalkiew 初期設定
        xwbFirstSet();

    }

    // 初期　View取得まとめ
    void findViewById2() {

        // XWalkView取得
        /*
        xWalkViews[TWITTER]= (XWalkView) findViewById(R.id.xwv_webbrows_twitter);
        xWalkViews[FACEBOOK] = (XWalkView) findViewById(R.id.xwv_webbrows_facebook);
        xWalkViews[MIXI] = (XWalkView) findViewById(R.id.xwv_webbrows_mixi);
        xWalkViews[GOOGLEPLUS] = (XWalkView) findViewById(R.id.xwv_webbrows_googleplus);
        */

        fl = (FrameLayout) findViewById(R.id.fl_web_brows);

        for (int i = 0; i < xWalkViews.length; i++) {
            xWalkViews[i] = new XWalkView(this, this);
            fl.addView(xWalkViews[i]);
        }


        // 下部４ボタンのImageView取得
        iv[TWITTER] = (ImageView) findViewById(R.id.iv_webbrows_twitter);
        iv[FACEBOOK] = (ImageView) findViewById(R.id.iv_webbrows_facebook);
        iv[MIXI] = (ImageView) findViewById(R.id.iv_webbrows_mixi);
        iv[GOOGLEPLUS] = (ImageView) findViewById(R.id.iv_webbrows_googleplus);

        // ボタン初期設定
        for (ImageView iv : this.iv) {
            iv.setBackgroundColor(Color.WHITE);
            ButtonEnable(false, iv);
        }

    }

    // XWalkView初期設定
    void xwbFirstSet() {

        // cookieマネージャーを使用
        cookieManager = new XWalkCookieManager();


        for (final XWalkView xview : xWalkViews) {

            // ページバックキー処理
            xview.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    XWalkView xv = (XWalkView) v;
                    // 現在表示されているWebViewを指定
                    if ( keyCode == KeyEvent.KEYCODE_BACK
                            && xv.getNavigationHistory().canGoBack() )
                    {
                        Log.d("aaaqaws", "1");
                        return false;
                    }
                    else
                    {
                        Log.d("aaaqaws", "2");
                        return true;
                    }
                }
            });

            //リソースクライアントをセット
            xview.setResourceClient(new ResourceClient(xview));
            //UIクライアントをセット
            xview.setUIClient(new UIClient(xview));


        }

        // SNSリログ用にcookie削除
        removeCookies();

        /*
        最初に表示するWebViewとロードするページの設定
        */
        if (USER_ACCOUNT[TWITTER][0].length() > 0) {
            showFlag = TWITTER;
            xWalkViews[TWITTER].load(TWITTER_LOGIN_URL, null);
        }
        if (USER_ACCOUNT[FACEBOOK][0].length() > 0) {
            if (showFlag < 0) showFlag = FACEBOOK;
            xWalkViews[FACEBOOK].load(FACEBOOK_LOGIN_URL, null);
        }
        if (USER_ACCOUNT[MIXI][0].length() > 0) {
            if (showFlag < 0) showFlag = MIXI;
            xWalkViews[MIXI].load(MIXI_LOGIN_URL, null);
        }
        if (USER_ACCOUNT[GOOGLEPLUS][0].length() > 0) {
            if (showFlag < 0) showFlag = GOOGLEPLUS;
            xWalkViews[GOOGLEPLUS].load(GOOGLEPLUS_LOGIN_URL, null);
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
            mUploadMessage = null;
        }
    }

    // XWalkView UIクライアント
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

    // XWalkView リソースクライアント
    class ResourceClient extends XWalkResourceClient {

        //　コンストラクタ
        public ResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
            Log.d("aaaRClient", "Load Failed:" + description);
            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        // ロード上書き
        @Override
        public boolean shouldOverrideUrlLoading(XWalkView xview, String url) {
            super.shouldOverrideUrlLoading(xview, url);

            // ログアウト時の処理
            if (url.contains(TWITTER_LOGOUT_URL)
                    || url.contains(FACEBOOK_LOGOUT_URL)
                    || url.contains(GOOGLEPLUS_LOGOUT_URL)
                    ) {

                // XWalkViewのロードを停止
                xview.stopLoading();
                // WebActivity終了
                finish();
            }

            // Falseで このままXWalkViewを使用
            return false;
        }

        // ロード開始時
        @Override
        public void onLoadStarted(XWalkView xview, String url) {
            super.onLoadStarted(xview, url);

            // ユーザーエージェント
            String useragent
                    = url.contains(M_FACEBOOK_COM_URL)
                    || url.contains(MIXI_URL)
                    || url.contains(GOOGLEPLUS_URL)
                    || url.contains(GOOGLEPLUS_LOGIN_DOMAIN)
                    || url.contains(YOUTUBE_URL) ? ANDROID_4_0_3 : FIREFOX;


            Log.d("aaaonLoadStarted", url);
            Log.d("aaauseragent", useragent);
            // XWalkViewにユーザーエージェントを設定
            xview.setUserAgentString(useragent);
        }

        //ロード完了時
        @Override
        public void onLoadFinished(XWalkView xview , String url) {
            super.onLoadFinished(xview, url);

            // どのXViewを表示するか
            int Select_View = -1;
            // どのJScriptを表示するか
            int js = -1;

            if   // Twitterログイン画面 オートログイン処理
                    (url.equals(TWITTER_LOGIN_URL)) {js = TWITTER;xview.stopLoading();}

            else // Facebookログイン画面 オートログイン処理
                if (url.contains(FACEBOOK_LOGIN_URL)) {js = FACEBOOK;xview.stopLoading();}

            else //Google+ログイン画面 オートログイン処理
                if (url.contains(GOOGLEPLUS_LOGIN_DOMAIN)) {js = GOOGLEPLUS;xview.stopLoading();}


            else // Twitterのログイン画面以外はボタン有効化
                if (url.contains(TWITTER_URL)) {Select_View = TWITTER;xview.stopLoading();}

            else // Facebookのログイン画面以外はボタン有効化
                if (url.contains(FACEBOOK_URL)) {Select_View = FACEBOOK;xview.stopLoading();}

            else // Google+のログイン画面以外はボタン有効化
                if (url.contains(GOOGLEPLUS_URL)) {Select_View = GOOGLEPLUS;xview.stopLoading();}


            if // Select_Viewに変更があればViewの切り替えと下部ボタンの設定変更
                (Select_View >= 0) {

                if // XWalkViewを入れ替え
                    (showFlag == Select_View) removeAddView(showFlag);

                // ログイン画面以外はボタン有効化
                ButtonEnable(true, iv[Select_View]);
            }
            // jsに変更があれば自動ログイン用Javascriptを実行
            else if(js >= 0) loadJS(xview, js);

            Log.d("aaaonLoadFinished", url);

        }

//        @Override
//        public WebResourceResponse shouldInterceptLoadRequest(XWalkView view, String url) {
//            try {
//
//                view.setAcce
//                URL u = new URL(url);
//                // HTTPコネクション取得
//                HttpURLConnection con = (HttpURLConnection) u.openConnection(Proxy.NO_PROXY);
//
//                // リクエストヘッダを設定
//                con.setRequestProperty("Accept-Language", "ja-JP");
//
//                return new WebResourceResponse(null, null, con.getInputStream());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return super.shouldInterceptLoadRequest(view, url);
//        }

    }

    // 下部ボタン処理
    public void onClick(View v) {

        // 可視化するXWalkViewの番号を選択する変数
        int next = -1;

        // クリック時の処理
        switch (v.getId()) {
            case R.id.iv_webbrows_twitter:      // Twitter
                next = TWITTER; break;
            case R.id.iv_webbrows_facebook:     // Facebook
                next = FACEBOOK; break;
            case R.id.iv_webbrows_mixi:         // mixi
                next = MIXI; break;
            case R.id.iv_webbrows_googleplus:  // Google+
                next = GOOGLEPLUS; break;
            case R.id.iv_webbrows_home:         // ホームボタン
                finish(); break;
            default:
        }

        // XWalkView表示
        removeAddView(next);
        showFlag = next;

    }

    // XWalkView(SNS)切り替え用 View追加＆削除
    void removeAddView(int id) {

        if (id < 0) return;

        // レイアウトをすべて削除
        fl.removeAllViews();

        // ブラウジングするSNSのViewを表示
        fl.addView(xWalkViews[id]);
    }

    // Javascript
    void loadJS(XWalkView xview, int snsview) {

        StringBuilder sb = new StringBuilder();

        switch (snsview) {
            case TWITTER:

                // Twitterオートログイン
                sb.append("javascript:document.getElementById(\"");
                sb.append(TWITTER_USER_INPUT_ID);
                sb.append("\").setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[TWITTER][0]);
                sb.append("\");document.getElementById(\"");
                sb.append(TWITTER_PASS_INPUT_ID);
                sb.append("\").setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[TWITTER][1]);
                sb.append("\");document.getElementsByTagName(\"form\")[0].submit();");

                break;
            case FACEBOOK:

                // Facebookオートログイン
                sb.append("javascript:document.getElementsByName(\"");
                sb.append(FACEBOOK_USER_INPUT_NAME);
                sb.append("\")[0].setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[FACEBOOK][0]);
                sb.append("\");document.getElementsByName(\"");
                sb.append(FACEBOOK_PASS_INPUT_NAME);
                sb.append("\")[0].setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[FACEBOOK][1]);
                sb.append("\");document.getElementsByTagName(\"form\")[0].submit();");

                break;
            case MIXI:
                // mixiオートログイン

                break;
            case GOOGLEPLUS:

                // GooglePlusオートログイン
                sb.append("javascript:document.getElementById(\"");
                sb.append(GOOGLEPLUS_USER_INPUT_ID);
                sb.append("\").setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[GOOGLEPLUS][0]);
                sb.append("\");document.getElementById(\"");
                sb.append(GOOGLEPLUS_PASS_INPUT_ID);
                sb.append("\").setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[GOOGLEPLUS][1]);
                sb.append("\");document.getElementById(\"");
                sb.append(GOOGLEPLUS_FORM_ID);
                sb.append("\").submit();");

                break;
        }

        // javascript実行
        xview.load(sb.toString(), "");

    }

    // 下部アカウントアイコンボタン有効設定
    void ButtonEnable(boolean flg, ImageView iv) {

        // ボタン有効無効設定
        iv.setEnabled(flg);

        if (flg) {
            // グレーフィルター解除
            iv.setColorFilter(0);
        } else {
            // ロードが完了していない又は、ログインしていないSNSはボタンの色を変更
            // ボタンをうすい灰色表示にする(非活性化ボタン風)
            iv.setColorFilter(Color.GRAY & 0xd0FFFFFF);
        }

    }

    // cookie削除
    void removeCookies() {

        // facebookはここでログアウトさせないので、cookieを退避
        String fCookie = cookieManager.getCookie(FACEBOOK_URL);

        // 全てのクッキーを削除してログアウトさせる
        cookieManager.removeAllCookie();

        // facebookのcookieを元に戻す
        cookieManager.setCookie(FACEBOOK_URL, fCookie);

    }

}
