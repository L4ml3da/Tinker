package burp.GUI;

import burp.IHttpRequestResponse;

import java.util.ArrayList;

public class linkTableData {
    public int pos;
    public String dataFromURL;
    public int apiNumbers;
    public int domainNumbers;

    public ArrayList<String> apiLinkData;
    public ArrayList<String> domainData;

    public String apiLinkDataText = "[Api Link Data]";
    public String domainDataText = "[Domain Data]";

    public linkTableData(int pos, String dataFromURL, int apiNumbers, int domainNumbers, ArrayList<String> apiLinkData, ArrayList<String> domainData) {
        this.pos = pos;
        this.dataFromURL = dataFromURL;
        this.apiNumbers = apiNumbers;
        this.domainNumbers = domainNumbers;
        this.apiLinkData = apiLinkData;
        this.domainData = domainData;
    }

    public void linkTableDisplayHandle() {
        for(String api : apiLinkData) {
            this.apiLinkDataText = this.apiLinkDataText + "\n" + api;
            this.apiNumbers += 1;
        }
        /*
        for(String d : domainData) {
            this.domainDataText = this.domainDataText" + "\n" + d;
            this.domainNumbers += 1;
        }
         */
    }
}
