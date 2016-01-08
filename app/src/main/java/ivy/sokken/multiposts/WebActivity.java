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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

public class WebActivity extends Activity implements Constants {


    private boolean debug = true;

    private XWalkView[] xWalkViews = new XWalkView[4];               // XWalkView
    private ImageView[] iv = new ImageView[4];                        // 下部ボタン
    private int showFlag = -1;                                       // XWalkView表示フラグ
    private final static int FILE_CHOOSER_RESULT_CODE = 1;      // ファイル選択用Intentの戻りの識別子
    private ValueCallback<Uri> mUploadMessage;                      // アップロードファイル
    private FrameLayout fl;                                           // XWalkView表示用枠
    private XWalkCookieManager cookieManager;                       // クッキー操作変数（リログイン用）

    private boolean[] logined = new boolean[4];                   // ログイン成否


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 画面自動回転
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        // レイアウトを配置
        setContentView(R.layout.webbrows);

        Intent intent = getIntent();
        // userIDとpasswordをMainActivityから受け取る
        for (int i = 0; i < USER.length; i++) {
            USER_ACCOUNT[i][0] = intent.getStringExtra(USER[i]);
            USER_ACCOUNT[i][1] = intent.getStringExtra(PASS[i]);
            logined[i] = true;
        }

        // View取得
        findViewById2();

        // XWalkiew 初期設定
        xwbFirstSet();

    }

    // 初期　View取得まとめ
    private void findViewById2() {

        // XWalkView取得
        /*
        xWalkViews[TWITTER]= (XWalkView) findViewById(R.id.xwv_webbrows_twitter);
        xWalkViews[FACEBOOK] = (XWalkView) findViewById(R.id.xwv_webbrows_facebook);
        xWalkViews[MIXI] = (XWalkView) findViewById(R.id.xwv_webbrows_mixi);
        xWalkViews[GOOGLEPLUS] = (XWalkView) findViewById(R.id.xwv_webbrows_googleplus);
        */

        fl = (FrameLayout) findViewById(R.id.fl_webbrows_brows);

        for (int i = 0; i < xWalkViews.length; i++) {
            xWalkViews[i] = new XWalkView(this);
            xWalkViews[i].setBackgroundColor(Color.WHITE);
        }


        // 下部４ボタンのImageView取得
        iv[TWITTER] = (ImageView) findViewById(R.id.iv_webbrows_twitter);
        iv[FACEBOOK] = (ImageView) findViewById(R.id.iv_webbrows_facebook);
        iv[MIXI] = (ImageView) findViewById(R.id.iv_webbrows_mixi);
        iv[GOOGLEPLUS] = (ImageView) findViewById(R.id.iv_webbrows_googleplus);

        // ボタン初期設定
        for (ImageView iv : this.iv) {
            iv.setOnClickListener(new ClickListener());
            iv.setOnLongClickListener(new ClickListener());
            iv.setBackgroundColor(Color.WHITE);
            ButtonEnable(false, iv);
        }

        View iv_home = findViewById(R.id.iv_webbrows_home);
        iv_home.setOnClickListener(new ClickListener());
        iv_home.setOnLongClickListener(new ClickListener());

    }

    // XWalkView初期設定
    private void xwbFirstSet() {

        // cookieマネージャーを使用
        cookieManager = new XWalkCookieManager();


        for (final XWalkView xview : xWalkViews) {
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

        if // リクエストコードがファイル選択用Intentの戻りの識別子と一致したら実行
            (requestCode == FILE_CHOOSER_RESULT_CODE) {

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
    private class UIClient extends XWalkUIClient {
        public UIClient(XWalkView xwalkView) {
            super(xwalkView);
        }

        @Override
        public void onFullscreenToggled(XWalkView xview, boolean enterFullscreen) {
            //super.onFullscreenToggled(xview, enterFullscreen);

            LinearLayout footer_bar = (LinearLayout) findViewById(R.id.ll_webbrows_ctrl);

            // フルスクリーンによるレイアウトバグ回避処理込
            if (enterFullscreen) {
                // ステータスバー非表示 フルスクリーン
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                footer_bar.setVisibility(View.GONE);
            }
            else {

                // ステータスバー表示 フルスクリーン解除
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                footer_bar.setVisibility(View.VISIBLE);
            }

        }

            // ファイル選択画面呼び出し
        @Override
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
    private class ResourceClient extends XWalkResourceClient {

        //　コンストラクタ
        public ResourceClient(XWalkView view) {
            super(view);
        }

        @Override
        public void onProgressChanged(XWalkView xview, int progressInPercent) {
            if (xview.equals(xWalkViews[TWITTER])) {
                if    // プログレスバービューを表示
                    (progressInPercent < 100) findViewById(R.id.pb_webbrows_twitter).setVisibility(View.VISIBLE);
                else // プログレスバービューを非表示
                    findViewById(R.id.pb_webbrows_twitter).setVisibility(View.GONE);

            } else if (xview.equals(xWalkViews[FACEBOOK])) {
                if    // プログレスバービューを表示
                        (progressInPercent < 100) findViewById(R.id.pb_webbrows_facebook).setVisibility(View.VISIBLE);
                else // プログレスバービューを非表示
                    findViewById(R.id.pb_webbrows_facebook).setVisibility(View.GONE);

            } else if (xview.equals(xWalkViews[MIXI])) {
                if    // プログレスバービューを表示
                        (progressInPercent < 100) findViewById(R.id.pb_webbrows_mixi).setVisibility(View.VISIBLE);
                else // プログレスバービューを非表示
                    findViewById(R.id.pb_webbrows_mixi).setVisibility(View.GONE);

            } else if (xview.equals(xWalkViews[GOOGLEPLUS])) {
                if    // プログレスバービューを表示
                        (progressInPercent < 100) findViewById(R.id.pb_webbrows_googleplus).setVisibility(View.VISIBLE);
                else // プログレスバービューを非表示
                    findViewById(R.id.pb_webbrows_googleplus).setVisibility(View.GONE);

            }
        }

        @Override
        public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
            log("aaaRClient", "Load Failed:" + description);
//            super.onReceivedLoadError(view, errorCode, description, failingUrl);
        }

        // ロード上書き
        @Override
        public boolean shouldOverrideUrlLoading(XWalkView xview, String url) {

            // ログアウト時の処理
            if (url.contains(TWITTER_LOGOUT_URL)
                    || url.contains(FACEBOOK_LOGOUT_URL)
                    || url.contains(MIXI_LOGOUT_URL)
                    || url.contains(GOOGLEPLUS_LOGOUT_URL)
                    ) {

                // XWalkViewのロードを停止
                xview.stopLoading();
                // WebActivity終了
                finish();
            }

//            return super.shouldOverrideUrlLoading(xview, url);
            log("aaaonLoadFS", "(" + xview.getTitle() +  ")" +  url);

            return false;
        }

        // ロード開始時
        @Override
        public void onLoadStarted(XWalkView xview, String url) {
            //super.onLoadStarted(xview, url);

            // ユーザーエージェント
            String useragent = ANDROID_4_0_3;
//            String useragent
//                    = url.contains(FACEBOOK_COM_URL)
//                    || url.contains(T_CO_URL)
//                    || url.contains(FACEBOOK_JS_IMG_URL)
//                    || url.contains(MIXI_URL)
//                    || url.contains(GOOGLEPLUS_URL)
//                    || url.contains(GOOGLEPLUS_LOGIN_DOMAIN)
//                    || url.contains(GOOGLEPLUS_GSTATIC_URL)
//                    || url.contains(YOUTUBE_URL) ? ANDROID_4_0_3 : FIREFOX;


            log("aaaonLoadStarted", "(" + xview.getTitle() +  ")" +  url);
            // XWalkViewにユーザーエージェントを設定
            xview.setUserAgentString(useragent);
        }

        //ロード完了時
        @Override
        public void onLoadFinished(XWalkView xview , String url) {
            //super.onLoadFinished(xview, url);

            // どのXViewを表示するか
            int Select_View = -1;
            // どのJScriptを表示するか
            int js = -1;



            if // Twitterログインエラー
               ( url.contains(TWITTER_LOGIN_CHECK_URL) && url.contains("username_or_email") ) {
                Select_View = TWITTER;
                logined[Select_View] = false;
                xview.load("javascript:document.getElementsByTagName(\"body\")[0].innerHTML = \"入力されたユーザー名またはパスワードに誤りがあります。\";", null);
            }
            else // Twitterログインエラーパスワードリセット
                if ( url.contains(TWITTER_PASS_RESET_URL) && url.contains("username_or_email") ) {
                Select_View = TWITTER;
                logined[Select_View] = false;
            }
            else // Facebookログインエラー
                if ( url.contains(FACEBOOK_LOGIN_CHECK_URL) ) {
                Select_View = FACEBOOK;
                logined[Select_View] = false;
                xview.load("javascript:document.getElementsByTagName(\"body\")[0].innerHTML = \"入力されたメールアドレスまたは携帯電話番号はアカウントと一致しません。\";", null);
            }
            else // mixiログインエラー
                if ( url.contains(MIXI_LOGIN_CHECK_URL ) && url.charAt(url.length()-2) != '0' ) {
                Select_View = MIXI;
                logined[Select_View] = false;
                xview.load("javascript:document.getElementsByTagName(\"body\")[0].innerHTML = document.getElementById(\"errorArea\").innerHTML;", null);
            }
            else // Google+ログインエラー
                if ( url.contains(GOOGLEPLUS_LOGIN_CHECK_URL )) {
                Select_View = GOOGLEPLUS;
                logined[Select_View] = false;
                xview.load("javascript:document.getElementsByTagName(\"body\")[0].innerHTML = document.getElementById(\"errormsg_0_Passwd\").innerHTML;", null);
            }


            else // Twitterログイン画面 オートログイン処理
                if (url.equals(TWITTER_LOGIN_URL)) {js = TWITTER;}

            else // Facebookログイン画面 オートログイン処理
                if (url.contains(FACEBOOK_LOGIN_URL) ) {js = FACEBOOK;}

            else // mixiログイン画面 オートログイン処理
                if (url.contains(MIXI_LOGIN_URL)
                        && "ソーシャル・ネットワーキング サービス [mixi(ミクシィ)]".equals(xview.getTitle()) ) {js = MIXI;}

            else //Google+ログイン画面 オートログイン処理
                if (url.contains(GOOGLEPLUS_LOGIN_URL)) {js = GOOGLEPLUS;}


            else // Twitterのログイン画面以外はボタン有効化
                if (url.contains(TWITTER_URL)) {
                    // Twitter公式アプリのインストール催促通知を削除
                    xview.load("javascript:a=document.getElementsByTagName(\"prompt\")[0];a.parentNode.removeChild(a);", null);
                    Select_View = TWITTER;
                }

            else // Facebookのログイン画面以外はボタン有効化
                if (url.contains(M_FACEBOOK_URL)) {Select_View = FACEBOOK;}

            else // mixiのログイン画面以外はボタン有効化
                if (url.contains(MIXI_JP_URL)
                        && !"ソーシャル・ネットワーキング サービス [mixi(ミクシィ)]".equals(xview.getTitle()) ) {Select_View = MIXI;}

            else // Google+のログイン画面以外はボタン有効化
                if (url.contains(GOOGLEPLUS_URL)) {Select_View = GOOGLEPLUS;}


            if // Select_Viewに変更があればViewの切り替えと下部ボタンの設定変更
                (Select_View >= 0) {

                if // 最初に表示するXWalkViewを指定
                    (showFlag == Select_View && fl.getChildCount() == 0) RemoveAddView(showFlag);


                if // ログイン画面以外はボタン有効化
                    ( !iv[Select_View].isEnabled() ) {
                    // ブラウザバック時にログイン画面を表示させない
                    xview.getNavigationHistory().clear();

                    // 下部ボタンを有効にする
                    ButtonEnable(true, iv[Select_View]);
                }
            }

            else // jsに変更があれば自動ログイン用Javascriptを実行
                if(js >= 0) loadJS(xview, js);

            log("aaaonLoadFinished", "(" + xview.getTitle() +  ")" +  url);

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

    private class ClickListener implements OnClickListener, OnLongClickListener {

        // 下部ボタン処理
        @Override
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
            RemoveAddView(next);
            showFlag = next;

        }


        // 下部ボタン長押し処理
        @Override
        public boolean onLongClick(View v) {

            switch (v.getId()) {
                case R.id.iv_webbrows_twitter:      // Twitter
                    if (logined[TWITTER]) xWalkViews[TWITTER].load(TWITTER_URL, null);
                    break;
                case R.id.iv_webbrows_facebook:     // Facebook
                    if (logined[FACEBOOK]) xWalkViews[FACEBOOK].load(M_FACEBOOK_URL, null);
                    break;
                case R.id.iv_webbrows_mixi:         // mixi
                    if (logined[MIXI]) xWalkViews[MIXI].load(MIXI_URL, null);
                    break;
                case R.id.iv_webbrows_googleplus:  // Google+
                    if (logined[GOOGLEPLUS]) xWalkViews[GOOGLEPLUS].load(GOOGLEPLUS_URL, null);
                    break;
                case R.id.iv_webbrows_home:         // ホームボタン
                    break;
                default:
            }

            return false;
        }
    }

        // XWalkView(SNS)切り替え用 View追加＆削除
    private void RemoveAddView(int id) {

        if ( id < 0
                || fl.getChildCount() > 0 && fl.getChildAt(0) == xWalkViews[id]) return;

        // レイアウトをすべて削除
        fl.removeAllViews();

        // ブラウジングするSNSのViewを表示
        fl.addView(xWalkViews[id]);
    }

    // Javascript
    private void loadJS(XWalkView xview, int snsview) {

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
                sb.append("javascript:document.getElementsByName(\"");
                sb.append(MIXI_USER_INPUT_NAME);
                sb.append("\")[0].setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[MIXI][0]);
                sb.append("\");document.getElementsByName(\"");
                sb.append(MIXI_PASS_INPUT_NAME);
                sb.append("\")[0].setAttribute(\"value\", \"");
                sb.append(USER_ACCOUNT[MIXI][1]);
                sb.append("\");document.getElementsByName(\"");
                sb.append(MIXI_FORM_NAME);
                sb.append("\")[0].submit();");

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
        log("aaaloadJS",sb.toString());

    }

    // 下部アカウントアイコンボタン有効設定
    private void ButtonEnable(boolean flg, ImageView iv) {

        // ボタン有効無効設定
        iv.setEnabled(flg);

        if (flg) {
            // カラーフィルター解除
            iv.setColorFilter(0);
        } else {
            // ロードが完了していない又は、ログインしていないSNSはボタンの色を変更
            // ボタンをうすい灰色表示にする(非活性化ボタン風)
            iv.setColorFilter(Color.GRAY & 0xd0FFFFFF);
        }

    }

    // cookie削除
    private void removeCookies() {

//        // facebookはここでログアウトさせないので、cookieを退避
//        String fCookie = cookieManager.getCookie(FACEBOOK_COM_URL);
//        String mfCookie = cookieManager.getCookie(M_FACEBOOK_URL);

        // 全てのクッキーを削除してログアウトさせる
        cookieManager.removeAllCookie();

//        // facebookのcookieを元に戻す
//        cookieManager.setCookie(FACEBOOK_COM_URL, fCookie);
//        cookieManager.setCookie(M_FACEBOOK_URL, mfCookie);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction()==KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    // デバッグ処理
    private void log(String tag, String msg){
        if (debug) Log.d(tag, msg);
    }
}
