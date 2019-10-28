package cn.rongcloud.sealcall;

public class UserProfile {
    private String userId;
    private String token;

    public UserProfile(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
