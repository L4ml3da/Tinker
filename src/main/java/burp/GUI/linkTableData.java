package burp.GUI;

import burp.IHttpRequestResponse;
import burp.core.httpTester;

import java.util.ArrayList;
import java.util.HashMap;

public class linkTableData {
    public int pos;
    public String dataFromURL;

    public httpTester ht;
    public void setHt(httpTester ht) {
        this.ht = ht;
    }
    public httpTester getHt() {
        return ht;
    }
    public int apiNumbers;
    public int domainNumbers;

    public enum SensitiveDataType {
        API,
        URL,
        IDCARD,
        PHONE,
        EMAIL,
        INERIP,
        DOMAIN
    }

    public enum TableDataType {
        MAP_DATA,
        TXT_DATA,
        DATA_NUMBER
    }

    public HashMap<SensitiveDataType, ArrayList<Object>> sensitiveDataMap = new HashMap<>();
    public HashMap<String, SensitiveDataType> sensitiveNameMap = new HashMap<>();

    public linkTableData(String dataFromURL) {
        this.pos = 0;
        this.dataFromURL = dataFromURL;
        initSensitiveNameMap();
    }

    public void initSensitiveNameMap () {
        sensitiveNameMap.put("Api", SensitiveDataType.API);
        sensitiveNameMap.put("Url", SensitiveDataType.URL);
        sensitiveNameMap.put("IDCard", SensitiveDataType.IDCARD);
        sensitiveNameMap.put("Phone", SensitiveDataType.PHONE);
        sensitiveNameMap.put("Email", SensitiveDataType.EMAIL);
        sensitiveNameMap.put("InterIP", SensitiveDataType.INERIP);
        sensitiveNameMap.put("Domain", SensitiveDataType.DOMAIN);
    }

    public String getTableTextData(String typeName) {
        SensitiveDataType realType = sensitiveNameMap.get(typeName);
        return (String) sensitiveDataMap.get(realType).get(TableDataType.valueOf("TXT_DATA").ordinal());
    }

    public int getTableDataNum(String typeName) {
        SensitiveDataType realType = sensitiveNameMap.get(typeName);
        return (int)sensitiveDataMap.get(realType).get(TableDataType.valueOf("DATA_NUMBER").ordinal());
    }

    public ArrayList<String> getAPIList() {
        return (ArrayList<String>) sensitiveDataMap.get(SensitiveDataType.API).get(TableDataType.valueOf("MAP_DATA").ordinal());
    }
    public void setSensitiveDataMap(SensitiveDataType type, ArrayList<String> data) {
        ArrayList<Object> tmpDataList = new ArrayList<>();
        tmpDataList.add(data);
        StringBuilder tmpDataTxt = new StringBuilder();
        int cnt = 0;
        for(String d : data) {
            tmpDataTxt.append("\n").append(d);
            cnt += 1;
        }
        tmpDataList.add(tmpDataTxt.toString());
        tmpDataList.add(cnt);
        sensitiveDataMap.put(type, tmpDataList);
    }
}
