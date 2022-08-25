package burp.GUI;

import java.net.StandardSocketOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class UIStatController {

    public boolean valueNeedSync = false;

    private static final ReentrantLock valueChangeStatlock = new ReentrantLock();
    public ArrayList<String> filterCode = new ArrayList<>();
    public ArrayList<String> supportMethod = new ArrayList<>();
    public boolean withCookie = false;
    public boolean autoRepeater = false;


    public enum stats {
        ON,
        OFF
    }

    public enum UICheckBoxType {
        Auto,
        WithCookie,
        Code404,
        Code403,
        Code401,
        Code500,
        MethodGET,
        MethodPost,
        MethodPut,
        MethodHead,
    }


    public HashMap<UICheckBoxType, stats> UIStatMap = new HashMap<>();
    public HashMap<UICheckBoxType, String> httpConfigStrMap = new HashMap<>();

    public UIStatController() {
        for(UICheckBoxType box : UICheckBoxType.values()) {
            UIStatMap.put(box, stats.OFF);
        }
        httpConfigStrMap.put(UICheckBoxType.Code404, "404");
        httpConfigStrMap.put(UICheckBoxType.Code403, "403");
        httpConfigStrMap.put(UICheckBoxType.Code401, "401");
        httpConfigStrMap.put(UICheckBoxType.Code500, "500");
        httpConfigStrMap.put(UICheckBoxType.MethodGET, "GET");
        httpConfigStrMap.put(UICheckBoxType.MethodPost, "POST");
        httpConfigStrMap.put(UICheckBoxType.MethodPut, "PUT");
        httpConfigStrMap.put(UICheckBoxType.MethodHead, "HEAD");
    }

    public void updateUIStatMap(UICheckBoxType boxType, stats nowStat) {
        UIStatMap.put(boxType, nowStat);
    }

    public boolean needSync() throws InterruptedException {
        if(valueChangeStatlock.tryLock(1, TimeUnit.SECONDS)){
            if(valueNeedSync) {
                valueChangeStatlock.unlock();
                return true;
            } else {
                valueChangeStatlock.unlock();
                return false;
            }
        }
        return false;
    }

    public void UIStatHandle(UICheckBoxType boxType, boolean selected) throws InterruptedException {
        while (true) {
            if(valueChangeStatlock.tryLock(1, TimeUnit.SECONDS)){
                valueNeedSync = true;
                if(selected) {
                    updateUIStatMap(boxType, stats.ON);
                } else {
                    updateUIStatMap(boxType, stats.OFF);
                }
                valueChangeStatlock.unlock();
                break;
            }
        }
    }

    public void syncUIStats() throws InterruptedException {
        filterCode.clear();
        supportMethod.clear();
        for(Map.Entry<UICheckBoxType, stats> entry : UIStatMap.entrySet()) {
            switch (entry.getKey()) {
                case Auto:
                    if(getCheckBoxStat(UICheckBoxType.Auto) == stats.ON) {
                        autoRepeater = true;
                    } else {
                        autoRepeater = false;
                    }
                    break;
                case WithCookie:
                    if(getCheckBoxStat(UICheckBoxType.WithCookie) == stats.ON) {
                        withCookie = true;
                    } else {
                        withCookie = false;
                    }
                    break;
                case Code404:
                    if(getCheckBoxStat(UICheckBoxType.Code404) == stats.ON) {
                        filterCode.add(httpConfigStrMap.get(UICheckBoxType.Code404));
                    }
                    break;
                case Code403:
                    if(getCheckBoxStat(UICheckBoxType.Code403) == stats.ON) {
                        filterCode.add(httpConfigStrMap.get(UICheckBoxType.Code403));
                    }
                    break;
                case Code401:
                    if(getCheckBoxStat(UICheckBoxType.Code401) == stats.ON) {
                        filterCode.add(httpConfigStrMap.get(UICheckBoxType.Code401));
                    }
                    break;
                case Code500:
                    if(getCheckBoxStat(UICheckBoxType.Code500) == stats.ON) {
                        filterCode.add(httpConfigStrMap.get(UICheckBoxType.Code500));
                    }
                    break;
                case MethodGET:
                    if(getCheckBoxStat(UICheckBoxType.MethodGET) == stats.ON) {
                        supportMethod.add(httpConfigStrMap.get(UICheckBoxType.MethodGET));
                    }
                    break;
                case MethodPost:
                    if(getCheckBoxStat(UICheckBoxType.MethodPost) == stats.ON) {
                        supportMethod.add(httpConfigStrMap.get(UICheckBoxType.MethodPost));
                    }
                    break;
                case MethodPut:
                    if(getCheckBoxStat(UICheckBoxType.MethodPut) == stats.ON) {
                        supportMethod.add(httpConfigStrMap.get(UICheckBoxType.MethodPut));
                    }
                    break;
                case MethodHead:
                    if(getCheckBoxStat(UICheckBoxType.MethodHead) == stats.ON) {
                        supportMethod.add(httpConfigStrMap.get(UICheckBoxType.MethodHead));
                    }
                    break;
            }

        }
        if(valueChangeStatlock.tryLock(1, TimeUnit.SECONDS)) {
            valueNeedSync = false;
            valueChangeStatlock.unlock();
        }
    }

    public stats getCheckBoxStat(UICheckBoxType boxType) {
        return UIStatMap.get(boxType);
    }
}
