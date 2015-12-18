package ivy.sokken.multiposts2;


public interface Constants {
    int TWITTER = 0;
    int FACEBOOK = 1;
    int MIXI = 2;
    int GOOGLEPLUS = 3;

    String FIREFOX = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";
    String ANDROID_3_0_1 = "Mozilla/5.0 (Linux; U; Android 3.0; ja-jp; Xoom Build/HRI39) AppleWebKit/525.10  (KHTML, like Gecko) Version/3.0.4 Mobile Safari/523.12.2";
    String ANDROID_4_0_3 = "Mozilla/5.0 (Linux; U; Android 4.0.3; ja-jp; KFTT Build/IML74K) AppleWebKit/535.19 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.13";
    String DOCOMO = "DoCoMo/2.0";

    String TWITTER_COM_URL = "twitter.com/";
    String TWITTER_URL = "https://mobile.twitter.com/";
    String TWITTER_LOGIN_URL = "https://mobile.twitter.com/session/new";
    String TWITTER_LOGOUT_URL = "http://mobile.twitter.com";
    String TWITTER_USER_INPUT_ID = "session[username_or_email]";
    String TWITTER_PASS_INPUT_ID = "session[password]";

    String M_FACEBOOK_COM_URL = "m.facebook.com";
    String FACEBOOK_URL ="https://m.facebook.com";
    String FACEBOOK_LOGIN_URL = "https://m.facebook.com/login.php";
    String FACEBOOK_LOGOUT_URL = "https://m.facebook.com/logout.php";
    String FACEBOOK_USER_INPUT_NAME = "email";
    String FACEBOOK_PASS_INPUT_NAME = "pass";

    String MIXI_JP_URL = "";
    String MIXI_URL = "mixi.jp";
    String MIXI_LOGIN_URL = "mixi.jp";
    String MIXI_LOGOUT_URL = "";
    String MIXI_USER_INPUT = "";
    String MIXI_PASS_INPUT = "";

    String PLUS_GOOGLE_COM_URL = "plus.google.com";
    String GOOGLEPLUS_URL = "https://plus.google.com";
    String GOOGLEPLUS_LOGIN_DOMAIN = "accounts.google.com";
    String GOOGLEPLUS_LOGIN_URL = "https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fplus.google.com";
    String GOOGLEPLUS_LOGOUT_URL = ".com/accounts/Logout";
    String GOOGLEPLUS_USER_INPUT_ID = "Email";
    String GOOGLEPLUS_PASS_INPUT_ID = "Passwd";
    String GOOGLEPLUS_FORM_ID = "gaia_loginform";


    String YOUTUBE_URL = "youtube.com/";

    String[][] USER_ACCOUNT = new String[4][2];

    String[] USER = {
            "twitter_user",
            "facebook_user",
            "mixi_user",
            "googleplus_user",
    };

    String[] PASS = {
            "twitter_pass",
            "facebook_pass",
            "mixi_pass",
            "googleplus_pass",
    };

}
