package edu.kau.cloudmac.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AckingActivatorCallable implements Callable<String>
{
	private static final Logger log = LoggerFactory.getLogger(AckingActivatorCallable.class);
	private String hostname;
	private int port;
	private byte[] mac;
	private int timeout;

    public AckingActivatorCallable(String hostname, int port, byte[] mac, int timeout)
    {
    	 this.hostname = hostname;
    	 this.port = port;
    	 this.mac = mac;
    	 this.timeout = timeout;
    }

    @Override
    public String call() throws Exception
    {
    	try
		{
    		log.trace("<<<-----");
		    Socket socket = new Socket(hostname, port);
		    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		    out.printf("lease %x:%x:%x:%x:%x:%x %d\n", mac[0], mac[1], mac[2], mac[3], mac[4], mac[5], timeout);
		    out.flush();
		    in.readLine();
		    socket.close();
		    log.trace("----->>>");
		}
		catch (Exception e)
		{
			log.error(e.toString());
		}

        return Thread.currentThread().getName();
    }
}

