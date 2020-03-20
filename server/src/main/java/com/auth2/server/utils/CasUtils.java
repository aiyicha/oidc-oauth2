/* 文件名: CasUtils.java
 *
 * 作者: yesj (yesj@enlink.cn)
 * 描述:
 *
 * Copyright @2020 Enlink, All Rights Reserved.
 */
package com.auth2.server.utils;

import java.util.Random;
import java.util.UUID;

public class CasUtils {

    private CasUtils() {
    }

    /**
     * 生成 serviceTicket
     *
     * @return serviceTicket ST-随机数字-UUID-USER_AGENT
     */
    public static String genServiceTicket() {
        // TODO userAgent 判空处理
        UUID uuid = UUID.randomUUID();
        Random random = new Random();
        return "ST-" + uuid + "-" + "ENLINK";
    }

    public static void main(String[] args) {
        String st = genServiceTicket();
        System.out.println(st);
        System.out.println(st.length());
    }

}
