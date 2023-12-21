package burp;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import burp.GUI.TinkerGUI;
import burp.GUI.UIStatController;
import burp.GUI.linkTableData;
import burp.core.CustomScanIssue;
import burp.core.jsParser;
import burp.core.httpTester;
import burp.GUI.repeaterTableData;


public class BurpExtender implements IBurpExtender, IScannerCheck, ITab {
    private IBurpExtenderCallbacks callbacks;
    private TinkerGUI tkGUI;
    private IExtensionHelpers tkHelps;


    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        callbacks.setExtensionName("Tinker");
        this.callbacks = callbacks;
        this.tkHelps = callbacks.getHelpers();
        try {
            tkGUI = new TinkerGUI(callbacks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        callbacks.addSuiteTab(this);
        callbacks.registerScannerCheck(this);
        callbacks.printOutput(
                "[+] Tinker load success\n" +
                        "[+] Good Luck ^_^\n" +
                        "[+]\n" +
                        "[+] #####################################\n" +
                        "[+]    Tinker v1.3.0\n" +
                        "[+]    author: L4ml3da\n" +
                        "[+] ####################################\n"
                );
    }

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        //String url = tkHelps.analyzeRequest(baseRequestResponse).getUrl().toString();
        List<IScanIssue> issues = new ArrayList<>();
        URL url = tkHelps.analyzeRequest(baseRequestResponse).getUrl();
        String mime = tkHelps.analyzeResponse(baseRequestResponse.getResponse()).getStatedMimeType();
        callbacks.printOutput("found url " + url);
        callbacks.printOutput("path " + url.getPath());
        callbacks.printOutput("mime " + mime);
        jsParser jsFinder = new jsParser(url, tkGUI.UIcon.filterBlackSuffix, tkGUI.UIcon.filterBlackURL);
        ArrayList<repeaterTableData> rdata;

        /*
        try {
            IHttpRequestResponse testreqResp = callbacks.makeHttpRequest(baseRequestResponse.getHttpService(), callbacks.getHelpers().buildHttpRequest(new URL("https://www.hrcsign.com/file/person/detail")));
            callbacks.printOutput("test url resp" + testreqResp.getResponse().toString());
            callbacks.printOutput("test url resp code" +  callbacks.getHelpers().analyzeResponse(testreqResp.getResponse()).getStatusCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        */
        if (jsFinder.isTargetFile(mime)) {
            callbacks.printOutput("js file need parser " + url);
            String respStr = new String(baseRequestResponse.getResponse(), StandardCharsets.UTF_8);
            //callbacks.printOutput(respStr);
            jsFinder.analyzeJS(respStr);
            if(jsFinder.notFoundAnything) {
                return null;
            }
            //callbacks.printOutput(allLink.toString());
            issues.add(new CustomScanIssue(baseRequestResponse.getHttpService(),
                    url,
                    new IHttpRequestResponse[]{baseRequestResponse},
                    "Tinker Explore",
                    "Tinker Explore: \n" +
                            "API: " + String.valueOf(jsFinder.ltd.getTableDataNum("Api")) + "\n" +
                            "Url: " + String.valueOf(jsFinder.ltd.getTableDataNum("Url")) + "\n" +
                            "IDCard: " + String.valueOf(jsFinder.ltd.getTableDataNum("IDCard")) + "\n" +
                            "Phone: " + String.valueOf(jsFinder.ltd.getTableDataNum("Phone")) + "\n" +
                            "Email: " + String.valueOf(jsFinder.ltd.getTableDataNum("Email")) + "\n" +
                            "InterIP: " + String.valueOf(jsFinder.ltd.getTableDataNum("InterIP")) + "\n" +
                            "Doamin: " + String.valueOf(jsFinder.ltd.getTableDataNum("Domain")) + "\n",
                    "Low"));
            httpTester tester = new httpTester(callbacks, baseRequestResponse, url);
            try {
                syncHttpConfig(tester, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            jsFinder.ltd.setHt(tester);
            tkGUI.updateLinkTable(jsFinder.ltd);
            if(tkGUI.UIcon.autoRepeater) {
                ArrayList<String> allAPI = jsFinder.ltd.getAPIList();
                if(!allAPI.isEmpty()){
                    try {
                        for(String uri : allAPI) {
                            try {
                                syncHttpConfig(tester, false);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            rdata = tester.testLinkReq(uri);
                            for(repeaterTableData res : rdata) {
                                tkGUI.updateRepeaterTable(res);
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    catch (Exception ex) {
                        callbacks.printOutput(ex.toString());
                    }
                }
            }
        }
        return issues;
    }

    public void syncHttpConfig(httpTester ht, boolean forceSync) throws InterruptedException {
        if(tkGUI.UIcon.needSync() || forceSync) {
            ht.configHttp(tkGUI.UIcon.filterCode, tkGUI.UIcon.supportMethod, tkGUI.UIcon.filterBlackMIME, tkGUI.UIcon.withCookie);
        }
    }

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        return null;
    }

    @Override
    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        return 0;
    }

    @Override
    public String getTabCaption() {
        return "Tinker";
    }

    @Override
    public Component getUiComponent() {
        return tkGUI.$$$getRootComponent$$$();
    }
}
