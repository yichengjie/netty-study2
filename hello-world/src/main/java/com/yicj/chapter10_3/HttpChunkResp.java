package com.yicj.chapter10_3;


import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
public class HttpChunkResp {
    public static void main(String[] args) throws Exception {
        String http = "http://localhost:8989/" ;
        URL realUrl = new URL(http);
        // 打开和URL之间的连接
        // URLConnection conn = realUrl.openConnection();
        HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
        conn.setRequestMethod("POST");
        // 设置通用的请求属性
        // *//*
        conn.setRequestProperty("accept", "");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setConnectTimeout(45*1000);
        conn.setReadTimeout(45*1000);
        // 获取URLConnection对象对应的输出流
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        // 发送请求参数
        out.print("?id=1");
        // flush输出流的缓冲
        out.flush();
        // 定义BufferedReader输入流来读取URL的响应
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line = "";
        // 流模式解析结果报文方式
        while (true) {
            if ((line = in.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                try {
                    //每接收一行完整数据直接写回前端。“\n”时前后端交互的自定义解析格式
                    if (!"0".equals(line) && line != null && !"".equals(line)) {
                        System.err.println(line);
                    }
                } catch (Exception e) {
                    log.error("error:" ,e);
                    continue;
                }
            } else {
                break;
            }
        }
    }
}
