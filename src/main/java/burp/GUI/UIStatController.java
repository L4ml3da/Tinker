package burp.GUI;

import java.net.StandardSocketOptions;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class UIStatController {

    public boolean valueNeedSync = false;

    private static final ReentrantLock valueChangeStatlock = new ReentrantLock();
    public ArrayList<String> filterCode = new ArrayList<>();
    public ArrayList<String> supportMethod = new ArrayList<>();

    public ArrayList<String> filterBlackSuffix ;
    public ArrayList<String> filterBlackURL;
    public ArrayList<String> filterBlackMIME;
    public boolean withCookie = false;
    public boolean autoRepeater = false;


    public enum UIConfigEnum {
        Auto,
        WithCookie,
        MethodGET,
        MethodPost,
        MethodPut,
        MethodHead,
        StatusCode,
        BlackSuffix,
        BlackURL,
        BlackMIME
    }

    public HashMap<UIConfigEnum, String> httpConfigStrMap = new HashMap<>();

    public UIStatController() {
        httpConfigStrMap.put(UIConfigEnum.MethodGET, "GET");
        httpConfigStrMap.put(UIConfigEnum.MethodPost, "POST");
        httpConfigStrMap.put(UIConfigEnum.MethodPut, "PUT");
        httpConfigStrMap.put(UIConfigEnum.MethodHead, "HEAD");
    }


    public boolean needSync() throws InterruptedException {
        if(valueChangeStatlock.tryLock(1, TimeUnit.SECONDS)){
            if(valueNeedSync) {
                valueNeedSync = false;
                valueChangeStatlock.unlock();
                return true;
            } else {
                valueChangeStatlock.unlock();
                return false;
            }
        }
        return false;
    }

    public boolean UIConfigHandle(UIConfigEnum type, boolean selected, String input) throws InterruptedException {
        while (true) {
            if(valueChangeStatlock.tryLock(2, TimeUnit.SECONDS)){
                break;
            }
        }
        switch (type) {
            case Auto:
                autoRepeater = selected;
                break;
            case WithCookie:
                withCookie = selected;
                break;
            case MethodGET:
                if (selected) {
                    if (!supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodGET))) {
                        supportMethod.add(httpConfigStrMap.get(UIConfigEnum.MethodGET));
                    }
                } else {
                    if (supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodGET))) {
                        supportMethod.remove(httpConfigStrMap.get(UIConfigEnum.MethodGET));
                    }
                }
                break;
            case MethodPost:
                if(selected) {
                    if (!supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodPost))) {
                        supportMethod.add(httpConfigStrMap.get(UIConfigEnum.MethodPost));
                    } else {
                        if (supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodPost))) {
                            supportMethod.remove(httpConfigStrMap.get(UIConfigEnum.MethodPost));
                        }
                    }
                }
                break;
            case MethodPut:
                if(selected) {
                    if (!supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodPut))) {
                        supportMethod.add(httpConfigStrMap.get(UIConfigEnum.MethodPut));
                    } else {
                        if (supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodPut))) {
                            supportMethod.remove(httpConfigStrMap.get(UIConfigEnum.MethodPut));
                        }
                    }
                }
                break;
            case MethodHead:
                if(selected) {
                    if (!supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodHead))) {
                        supportMethod.add(httpConfigStrMap.get(UIConfigEnum.MethodHead));
                    } else {
                        if (supportMethod.contains(httpConfigStrMap.get(UIConfigEnum.MethodHead))) {
                            supportMethod.remove(httpConfigStrMap.get(UIConfigEnum.MethodHead));
                        }
                    }
                }
                break;
            case StatusCode:
                String[] codes = input.replaceAll("\\s+", "").split(",");
                for (String code : codes) {

                    int statusCode;
                    try {
                        statusCode = Integer.parseInt(code);
                    } catch (NumberFormatException e) {
                        return false;
                    }

                    if (statusCode < 100 || statusCode >= 600) {
                        return false;
                    }
                }
                List<String> temp = Arrays.asList(codes);
                filterCode = new ArrayList<>(temp);
                break;
            case BlackSuffix:
                String[] bs = input.replaceAll("\\s+", "").split(",");
                filterBlackSuffix = new ArrayList<>(Arrays.asList(bs));
                break;
            case BlackURL:
                String[] bl = input.replaceAll("\\s+", "").split(",");
                filterBlackURL = new ArrayList<>(Arrays.asList(bl));
                break;
            case BlackMIME:
                String[] bm = input.replaceAll("\\s+", "").split(",");
                filterBlackMIME = new ArrayList<>(Arrays.asList(bm));
                break;
            default:
                break;
        }
        valueNeedSync = true;
        valueChangeStatlock.unlock();
        return true;
    }

}
