package net.java.dev.messageapi.quickstart;

import java.io.*;

import javax.enterprise.inject.Produces;

import com.oneandone.consumer.messageapi.adapter.xml.*;

public class ChatFactory {
	@Produces
	ChatApi create() {
		Writer writer = new OutputStreamWriter(System.out);
		return ToXmlEncoder.create(ChatApi.class, writer);
	}
}
