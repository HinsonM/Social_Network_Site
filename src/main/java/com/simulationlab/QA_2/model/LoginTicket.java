package com.simulationlab.QA_2.model;
/*
 从微信或者淘宝登录对应的网站时不需要输入账户密码，或者说用淘宝登录支付宝
 都是类似的道理，在出发端捎带上ticket，然后服务器从数据库中验证g该ticket，
and so on


logom


编程，细节，网站的补充
url=http://www.baidu.com， next非站内网站，非登录挑战跳到恶意网站，流量劫持
正则表达式匹配邮箱名是否合法
*/


import java.util.Date;

public class LoginTicket {
    private int id;
    private int userId;
    private String ticket;
    private Date expired;
    private int status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
