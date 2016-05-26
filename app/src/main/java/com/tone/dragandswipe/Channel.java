package com.tone.dragandswipe;

import java.io.Serializable;

/**
 * Created by zhaotong on 2016/5/25.
 */
public class Channel implements Serializable {
    private int channelId;
    private String channelName;
    private String channelUrl;
    private int isFixed; //表示栏目固定标识，0 ：不可编辑(拖动排序和订阅操作)， 1 : 可编辑
    private int isShow; //是否显示 ,0显示   1显示

    public Channel(int channelId, String channelName, String channelUrl, int isFixed, int isShow) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.channelUrl = channelUrl;
        this.isFixed = isFixed;
        this.isShow = isShow;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelUrl() {
        return channelUrl;
    }

    public void setChannelUrl(String channelUrl) {
        this.channelUrl = channelUrl;
    }

    public int getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(int isFixed) {
        this.isFixed = isFixed;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }
}
