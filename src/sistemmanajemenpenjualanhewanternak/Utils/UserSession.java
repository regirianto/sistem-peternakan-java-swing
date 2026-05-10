/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemmanajemenpenjualanhewanternak.Utils;

/**
 *
 * @author regi.suyono
 */
public class UserSession {
    private static int loginId;
    private static String loginName;

    public static void setLogin(int id, String nama) {
        loginId = id;
        loginName = nama;
    }

    public static int getLoginId() { return loginId; }
    
    public static String getLoginName() { return loginName; }

    public static void logout() {
        loginId = 0;
        loginName = null;
    }
}