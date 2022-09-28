package burp.GUI;

import burp.IHttpRequestResponse;

public class repeaterTableData {
    public int pos;
    public String domain;
    public String link;
    public int status;
    public int lens;
    public String mimeType;
    public IHttpRequestResponse requestResponse;

    public repeaterTableData(int pos, String domain, String link, int status, int lens, String mime, IHttpRequestResponse requestResponse) {
        this.pos = pos;
        this.domain = domain;
        this.link = link;
        this.status = status;
        this.lens = lens;
        this.mimeType = mime;
        this.requestResponse = requestResponse;
    }

    public String[] getRepeaterTableData () {
        return new String[]{String.valueOf(this.pos), this.domain, this.link, String.valueOf(this.status), String.valueOf(this.lens)};
    }
}
