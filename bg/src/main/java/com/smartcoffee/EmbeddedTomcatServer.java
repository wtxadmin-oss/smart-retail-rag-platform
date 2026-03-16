package com.smartcoffee;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.WebResourceRoot;

import java.io.File;

public class EmbeddedTomcatServer {
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("server.port", "8080"));
        String contextPath = System.getProperty("app.contextPath", "");
        String webappDir = System.getProperty("server.webappDir", "src/main/webapp");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector().setURIEncoding("UTF-8"); // ensure request URI decoding
        tomcat.getConnector().setUseBodyEncodingForURI(true);

        File webApp = new File(webappDir);
        // Explicitly use empty string for root context path and ensure absolute path
        Context ctx = tomcat.addWebapp("", webApp.getAbsolutePath());
        
        // 再次确认 Tomcat 实际设置的 Context Path
        System.out.println("Tomcat Context Path: " + ctx.getPath());

        // 配置类路径资源
        File classes = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", classes.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("Embedded Tomcat started at http://localhost:" + port + contextPath);
        tomcat.getServer().await();
    }
}
