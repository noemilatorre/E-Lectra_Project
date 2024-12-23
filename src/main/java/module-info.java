module com.electra.canbusdemo {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires peak.can.basic;
    requires eu.hansolo.tilesfx;
    requires eu.hansolo.medusa;


    opens com.electra.canbusdemo to javafx.fxml;
    exports com.electra.canbusdemo;

}