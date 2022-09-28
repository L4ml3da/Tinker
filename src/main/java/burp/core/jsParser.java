package burp.core;


import burp.GUI.linkTableData;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class jsParser {

    public URL url;
    static String[] blackJSFile = {"jquery", "google-analytics", "gpt.js"};
    static String[] blackSuffix = {"png", "jpg", "gif", "css", "js", "ico", "svg", "eot", "woff", "woff2", "ttf", "vue"};
    static String[] blackURL = {"www.w3.org", "localhost"};
    public linkTableData ltd;
    public boolean notFoundAnything = true;

    public jsParser(URL url) {
        this.url = url;
        this.ltd = new linkTableData(url.toString());
    }

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

    public Boolean isTargetFile (String mime) {
        String uri = url.getPath();
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

    public void analyzeJS(String jsData) {
        ArrayList<String> revData;

        revData = apiFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.API, revData);

        revData = urlFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.URL, revData);

        revData = idCardFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.IDCARD, revData);

        revData = phoneFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.PHONE, revData);

        revData = emailFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.EMAIL, revData);

        revData = interIPFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.INERIP, revData);

        revData = domainFinder(jsData);
        ltd.setSensitiveDataMap(linkTableData.SensitiveDataType.DOMAIN, revData);
    }

    public ArrayList<String> apiFinder(String jsData) {
        boolean useful;
        ArrayList<String> arrayList = new ArrayList<>();
        Pattern pattern= Pattern.compile(regex_str);
        Matcher matcher= pattern.matcher(jsData);
        while(matcher.find()) {
            useful = true;
            String s = matcher.group();
            s = s.replace("\"", "");
            s = s.replace("'", "");
            for (String x : blackSuffix) {
                if (s.endsWith(x) || s.startsWith("http")) {
                    useful = false;
                    break;
                }
            }
            int index = 0;
            if (useful) {
                for (int i = 0; i < s.length(); i++) {
                    if(Character.isLetterOrDigit(s.toCharArray()[i])){
                        index = i;
                        break;
                    }
                }
                s = s.substring(index);
                arrayList.add(s);
            }
        }
        if(arrayList.size() != 0) {
            arrayList = new ArrayList<>(new HashSet<>(arrayList));
            this.notFoundAnything = false;
        }
        return arrayList;
    }

    public ArrayList<String> interIPFinder(String str){
        ArrayList<String> interIP = new ArrayList<>();
        String in_ip = "\\b(?:(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}))|(?:172\\.(?:(?:1[6-9])|(?:2\\d)|(?:3[01]))\\.\\d{1,3}\\.\\d{1,3})|(?:192\\.168\\.\\d{1,3}\\.\\d{1,3})";
        Matcher matcher = Pattern.compile(in_ip).matcher(str);
        while (matcher.find()){
            interIP.add(matcher.group());
        }
        if(interIP.size() != 0) {
            interIP = new ArrayList<>(new HashSet<>(interIP));
            this.notFoundAnything = false;
        }
        return interIP;
    }

    public ArrayList<String> emailFinder(String str){
        ArrayList<String> email = new ArrayList<>();
        String is_email = "\\b[\\w-]+(?:\\.[\\w-]+)*@([\\w](?:[\\w-]*[\\w])?\\.)+(?:((?!png))((?!jpg))((?!jpeg))((?!gif))((?!ico))((?!html))((?!js))((?!css)))[A-Za-z]{2,6}";
        Matcher matcher = Pattern.compile(is_email).matcher(str);
        while (matcher.find()){
            email.add(matcher.group());
        }
        if(email.size() != 0) {
            email = new ArrayList<>(new HashSet<>(email));
            this.notFoundAnything = false;
        }
        return email;
    }

    public ArrayList<String> urlFinder(String str){
        ArrayList<String> url = new ArrayList<>();
        String tmpURL = "";
        boolean useful = true;
        String is_url = "([\"|'](http|https):\\/\\/([\\w.]+\\/?)\\S*?[\"|'])";
        Matcher matcher = Pattern.compile(is_url).matcher(str);
        while (matcher.find()){
            useful = true;
            tmpURL = matcher.group();
            for(String x : blackURL){
                if(tmpURL.contains(x)){
                    useful = false;
                    break;
                }
            }
            if(useful){
                tmpURL = tmpURL.replace("\"", "");
                tmpURL = tmpURL.replace("'", "");
                url.add(tmpURL);
            }
        }
        if(url.size() != 0) {
            url = new ArrayList<>(new HashSet<>(url));
            this.notFoundAnything = false;
        }
        return url;
    }

    public ArrayList<String> idCardFinder(String str){
        ArrayList<String> id = new ArrayList<>();
        String is_id = "\\b[1-9]\\d{5}(?:19|20)\\d\\d(?:0[1-9]|1[012])(?:0[1-9]|[12]\\d|3[01])\\d{3}(?:\\d|X|x)";
        Matcher matcher = Pattern.compile(is_id).matcher(str);
        while (matcher.find()){
            id.add(matcher.group());
        }
        if(id.size() != 0) {
            id = new ArrayList<>(new HashSet<>(id));
            this.notFoundAnything = false;
        }
        return id;
    }

    public ArrayList<String> phoneFinder(String str){
        ArrayList<String> phones = new ArrayList<>();
        String is_number = "\\b\\d{11,}";
        Matcher matcher = Pattern.compile(is_number).matcher(str);
        while (matcher.find())
        {
            if (matcher.group().length() == 11) {
                String is_phone = "^(13[0-9]|14[5|7]|15[^4]|17[6-9]|18[0-9])\\d{8}$";
                Matcher matcher2 = Pattern.compile(is_phone).matcher(matcher.group());
                while (matcher2.find()) {
                    phones.add(matcher2.group());
                }
            }
        }
        if(phones.size() != 0) {
            phones = new ArrayList<>(new HashSet<>(phones));
            this.notFoundAnything = false;
        }
        return phones;
    }
    public ArrayList<String> domainFinder(String str){
        ArrayList<String> domains = new ArrayList<>();
        String regx_domain = "^[a-zA-Z0-9]+([a-zA-Z0-9\\-\\.]+)?\\.(com|org|net|mil|edu|cn|COM|ORG|NET|MIL|EDU|CN)$";
        Matcher matcher = Pattern.compile(regx_domain).matcher(str);
        while (matcher.find()){
            domains.add(matcher.group());
        }
        if(domains.size() != 0) {
            domains = new ArrayList<>(new HashSet<>(domains));
            this.notFoundAnything = false;
        }
        return domains;
    }

}
