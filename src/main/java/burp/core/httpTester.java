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
    public ArrayList<String> blackMIME = new ArrayList<>();
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

    public void configHttp(ArrayList<String> fcode, ArrayList<String> sm, ArrayList<String> bm, boolean withcookie) {
        filterCode.clear();
        filterCode.addAll(fcode);

        supportMethod.clear();
        supportMethod.addAll(sm);

        blackMIME.clear();
        blackMIME.addAll(bm);

        withCookie = withcookie;
    }

    public ArrayList<repeaterTableData> testLinkReq(String uri) throws MalformedURLException, InterruptedException {
        IHttpRequestResponse testreqResp;
        int statCode, testLens;
        URL testURL;
        byte[] req;
        String mimeType;
        ArrayList<List<String>> hList = buildRequest(uri);
        ArrayList<repeaterTableData> dataList = new ArrayList<>();

        testURL = new URL(this.baseURL + "/" + uri);
        boolean dropHttp;
        for(List<String> h : hList) {
            dropHttp = false;
            if (h.get(0).toUpperCase().startsWith("POST") || h.get(0).toUpperCase().startsWith("PUT")) {
                req = helpers.buildHttpMessage(h, "test".getBytes());
            } else {
                req = helpers.buildHttpMessage(h, null);
            }
            try {
                testreqResp = callbacks.makeHttpRequest(http_service, req);
                statCode = callbacks.getHelpers().analyzeResponse(testreqResp.getResponse()).getStatusCode();
                mimeType = callbacks.getHelpers().analyzeResponse(testreqResp.getResponse()).getStatedMimeType();
                if(filterCode.contains(Integer.toString(statCode))) {
                    callbacks.printOutput("test url resp code " + statCode + " drop");
                    continue;
                }
                if(mimeType.length() != 0) {
                    for(String mt : blackMIME ) {
                        if(mt.toUpperCase().contains(mimeType.toUpperCase())) {
                            dropHttp = true;
                            break;
                        }
                    }
                }
                if(dropHttp) {
                    continue;
                }
                callbacks.printOutput("test url " + testURL.getHost() +  " " + testURL.getPath() +  " resp code" + statCode);
                testLens = testreqResp.getResponse().length;
                dataList.add(new repeaterTableData(0, testURL.getHost(), testURL.getPath(), statCode, testLens, mimeType, testreqResp));
            } catch (Exception e) {
                callbacks.printOutput(e.toString());
            }
        }
        return dataList;
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
