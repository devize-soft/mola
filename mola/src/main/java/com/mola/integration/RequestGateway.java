package com.mola.integration;

/**
 * Created by bilgi on 3/8/15.
 */
public interface RequestGateway {

    public String receive(String message);

    public void send();
}
