package cn.jiangzehui.hx.model;

import java.io.Serializable;

/**
 * Created by jiangzehui on 17/6/3.
 */
public class Emoji implements Serializable {
    int imageUri;
    String content;

    public int getImageUri() {
        return imageUri;
    }

    public void setImageUri(int imageUri) {
        this.imageUri = imageUri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
