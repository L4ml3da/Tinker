package burp.core;

import burp.*;
import burp.GUI.repeaterTableData;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class httpTester  {
    public IExtensionHelpers helpers;
    public IHttpService http_service;
    public IBurpExtenderCallbacks callbacks;
    public IHttpRequestResponse baseReqResp;

    public String baseURL;
    public ArrayList<String> filterCode = new ArrayList<>();
    public ArrayList<String> supportMethod = new ArrayList<>();
    public boolean withCookie = false;

    public httpTester(IBurpExtenderCallbacks callbacks, IHttpRequestResponse baseReqResp, URL singleURL) {
        this.baseReqResp = baseReqResp;
        this.callbacks = callbacks;
        this.http_service = baseReqResp.getHttpService();;
        this.helpers = callbacks.getHelpers();
        if(singleURL.getPort() != -1){
            this.baseURL = singleURL.getProtocol() + "://" + singleURL.getHost()+ ":" + singleURL.getPort();
        } else {
            this.baseURL = singleURL.getProtocol() + "://" + singleURL.getHost();
        }
    }

    public void configHttp(ArrayList<String> fcode, ArrayList<String> sm, boolean withcookie) {
        filterCode = fcode;
        supportMethod = sm;
        withCookie = withcookie;
    }

    public ArrayList<repeaterTableData> testLinkReq(String uri) throws MalformedURLException {
        IHttpRequestResponse testreqResp;
        int statCode, testLens;
        URL testURL;
        byte[] req;
        ArrayList<List<String>> hList = buildRequest(uri);
        ArrayList<repeaterTableData> dataList = new ArrayList<>();

        testURL = new URL(this.baseURL + "/" + uri);
        for(List<String> h : hList) {
            req = helpers.buildHttpMessage(h, "test".getBytes());
            testreqResp = callbacks.makeHttpRequest(http_service, req);
            statCode = callbacks.getHelpers().analyzeResponse(testreqResp.getResponse()).getStatusCode();
            if(filterCode.contains(Integer.toString(statCode))) {
                callbacks.printOutput("test url resp code " + statCode + " drop");
                continue;
            }
            callbacks.printOutput("test url resp code" + statCode);
            testLens = testreqResp.getResponse().length;
            dataList.add(new repeaterTableData(0, testURL.getHost(), testURL.getPath(), statCode, testLens, testreqResp));
        }
        return dataList;
        /*
        testURL = new URL(this.baseURL + "/" + uri);
        callbacks.printOutput("test url " + testURL.toString());
        testreqResp = callbacks.makeHttpRequest(http_service, helpers.buildHttpRequest(testURL));
        callbacks.printOutput("test url resp" + testreqResp.getResponse().toString());
        statCode = callbacks.getHelpers().analyzeResponse(testreqResp.getResponse()).getStatusCode();
        callbacks.printOutput("test url resp code" + statCode);
        testLens = testreqResp.getResponse().length;
        return new repeaterTableData(0, testURL.getHost(), testURL.getPath(), statCode, testLens, testreqResp);
         */
    }

    public ArrayList<List<String>> buildRequest(String uri) {
        ArrayList<List<String>> headerList = new ArrayList<>();
        List<String> tmpHeader;
        List<String> headers = callbacks.getHelpers().analyzeRequest(baseReqResp).getHeaders();
        if(!withCookie) {
            for (int i = 0; i < headers.size(); i++)
            {
                if (headers.get(i).startsWith("Cookie:"))
                {
                    headers.remove(i);
                    callbacks.printOutput("has remove cookie");
                    break;
                }
            }
        }
        callbacks.printOutput("support method ");
        callbacks.printOutput(supportMethod.toString());
        if(supportMethod.size() != 0) {
            for(String method : supportMethod) {
                tmpHeader = new ArrayList<>(headers);
                tmpHeader.set(0, method + " /" + uri + " HTTP/1.1");
                headerList.add(tmpHeader);
            }
        } else {
            //default support GET
            tmpHeader = new ArrayList<>(headers);
            tmpHeader.set(0, "GET" + " /" + uri + " HTTP/1.1");
            headerList.add(tmpHeader);
        }

        return headerList;
    }
}
