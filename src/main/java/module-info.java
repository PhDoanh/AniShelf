module com.library.anishelf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires annotations;
    requires java.mail;
    requires com.google.zxing.javase;
    requires com.google.zxing;
    requires opencv;
    requires java.net.http;
    requires com.google.gson;
    requires mysql.connector.j;
    requires org.json;
    requires com.google.api.services.books;
    requires com.google.common;
    requires google.api.client;
    requires com.google.api.client;
    requires google.api.services.youtube.v3.rev222;
    requires com.google.api.client.json.jackson2;
    requires org.checkerframework.checker.qual;
    requires javafx.media;
    requires java.desktop;
    requires java.sql;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires atlantafx.base;
    requires com.github.benmanes.caffeine;
    requires net.dv8tion.jda;

    opens com.library.anishelf.controller to javafx.fxml;
    opens com.library.anishelf to javafx.fxml;
    exports com.library.anishelf;
    exports com.library.anishelf.controller;
    exports com.library.anishelf.model;
    exports com.library.anishelf.service;
    opens com.library.anishelf.service to javafx.fxml;
    exports com.library.anishelf.util.config;
}