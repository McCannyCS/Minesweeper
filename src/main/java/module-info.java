module homelet.mccanny.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires org.apache.logging.log4j;
    requires spring.beans;
    requires spring.core;
    requires spring.context;

    opens homelet.mccanny.minesweeper to javafx.fxml, spring.core;
    opens homelet.mccanny.minesweeper.config to spring.core;
    opens homelet.mccanny.minesweeper.service to spring.core;
    opens homelet.mccanny.minesweeper.controller to javafx.fxml, spring.core;
    opens homelet.mccanny.minesweeper.repository to spring.core;
    opens homelet.mccanny.minesweeper.application to javafx.fxml, spring.core;

    exports homelet.mccanny.minesweeper;
    exports homelet.mccanny.minesweeper.model.game;
    exports homelet.mccanny.minesweeper.model.leaderboard;
    exports homelet.mccanny.minesweeper.config;
    exports homelet.mccanny.minesweeper.service;
    exports homelet.mccanny.minesweeper.controller;
    exports homelet.mccanny.minesweeper.repository;
    exports homelet.mccanny.minesweeper.application;
}