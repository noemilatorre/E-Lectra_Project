package com.electra.canbusdemo.CANbus;
//per ricevere dati (tramite callback)
public interface Notifiable {

     void _notify(String data);
}
