package ivy.sokken.multiposts;

/**
 * Created by 131029 on 2015/12/01.
 */
public interface Variable {
    int TWITTER = 0;
    int FACEBOOK = 1;
    int MIXI = 2;
    int GOOGLEPLUS = 3;

    String FIREFOX = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";
    String ANDROID = "Mozilla/5.0 (Linux; U; Android 3.0.1; ja-jp; A500 Build/HRI66) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";
    String DOCOMO = "DoCoMo/2.0";

    String TWITTER_COM_URL = "twitter.com/";
    String TWITTER_URL = "https://mobile.twitter.com/";
    String TWITTER_LOGIN_URL = "https://mobile.twitter.com/session/new";
    String TWITTER_LOGOUT_URL = "http://mobile.twitter.com";
    String TWITTER_USER_INPUT = "session[username_or_email]";
    String TWITTER_PASS_INPUT = "session[password]";

    String M_FACEBOOK_COM_URL = "m.facebook.com";
    String FACEBOOK_URL ="https://m.facebook.com";
    String FACEBOOK_LOGIN_URL = "https://m.facebook.com";
    String FACEBOOK_LOGOUT_URL = "https://m.facebook.com/logout.php";
    String FACEBOOK_USER_INPUT = "";
    String FACEBOOK_PASS_INPUT = "";

    String MIXI_JP_URL = "";
    String MIXI_URL = "";
    String MIXI_LOGIN_URL = "";
    String MIXI_LOGOUT_URL = "";
    String MIXI_USER_INPUT = "";
    String MIXI_PASS_INPUT = "";

    String PLUS_GOOGLE_COM_URL = "plus.google.com";
    String GOOGLEPLUS_URL = "https://plus.google.com";
    String GOOGLEPLUS_LOGIN_URL = "https://accounts.google.com/ServiceLogin?continue=https://plus.google.com/explore";
    String GOOGLEPLUS_LOGOUT_URL = ".com/accounts/Logout";
    String GOOGLEPLUS_USER_INPUT = "";
    String GOOGLEPLUS_PASS_INPUT = "";


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
