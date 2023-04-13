package org.example.security.custom;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class LoginUserInfoHelper {
    private static ThreadLocal<Long> userId = new ThreadLocal<>();

    private static ThreadLocal<String> userName = new ThreadLocal<>();

    public static void setUserId(Long _userId) {
        userId.set(_userId);
    }
    public static Long getUserId() {
        return userId.get();
    }
    public static void removeUserId() {
        userId.remove();
    }
    public static void setUserName(String _username) {
        userName.set(_username);
    }
    public static String getUserName() {
        return userName.get();
    }
    public static void removeUserName() {
        userName.remove();
    }
}
