package burp.core;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class jsParser {

    //static List blackJSFile = new ArrayList<String>(){{"jquery", "google"}};
    static String[] blackJSFile = {"jquery", "google-analytics", "gpt.js"};
    static String[] blackSuffix = {"png", "jpg", "gif", "css", "js", "ico", "svg", "eot", "woff", "woff2", "ttf"};

    static String regex_str = "(?:\"|')" + "(" +
             "((?:[a-zA-Z]{1,10}://|//)" +
            "[^\"'/]{1,}\\." +
            "[a-zA-Z]{2,}[^\"']{0,})" +
            "|" +
            "((?:/|\\.\\./|\\./)" +
            "[^\"'><,;| *()(%%$^/\\\\\\[\\]]" +
            "[^\"'><,;|()]{1,})"+
            "|" +
            "([a-zA-Z0-9_\\-/]{1,}/" +
            "[a-zA-Z0-9_\\-/]{1,}" +
            "\\.(?:[a-zA-Z]{1,4}|action)" +
            "(?:[\\?|/][^\"|']{0,}|))" +
            "|" +
            "([a-zA-Z0-9_\\-]{1,}" +
            "\\.(?:php|asp|aspx|jsp|json|" +
            "action|html|js|txt|xml)" +
            "(?:\\?[^\"|']{0,}|))" +
            ")" +
            "(?:\"|')";

    public Boolean isTargetFile (String uri, String mime) {
        if(!uri.endsWith(".js") || !mime.toLowerCase().contains("script")) {
            return false;
        }
        for (String x : blackJSFile) {
            if(uri.toLowerCase().contains(x)) {
                return false;
            }
        }
        return true;
    }

    public ArrayList<String> analyzeJS(String jsData) {
        boolean useful;
        ArrayList<String> arrayList = new ArrayList<>();
        Pattern pattern= Pattern.compile(regex_str);
        Matcher matcher= pattern.matcher(jsData);
        while(matcher.find()) {
            useful = true;
            String s = matcher.group();
            s = s.replace("\"", "");
            for (String x : blackSuffix) {
                if (s.endsWith(x) || s.startsWith("http")) {
                    useful = false;
                    break;
                }
            }
            int index = 0;
            if (useful) {
                for (int i = 0; i < s.length(); i++) {
                    if(s.toCharArray()[i] != '.' && s.toCharArray()[i] != '/'){
                        index = i;
                        break;
                    }
                }
                s = s.substring(index);
                arrayList.add(s);
            }
        }
        arrayList = new ArrayList<>(new HashSet<>(arrayList));
        return arrayList;
    }



}
