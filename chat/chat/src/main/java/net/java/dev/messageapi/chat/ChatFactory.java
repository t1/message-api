package net.java.dev.messageapi.chat;

import java.io.*;

import javax.enterprise.inject.Produces;

import com.oneandone.consumer.messageapi.adapter.xml.ToXmlEncoder;

import net.java.dev.messageapi.ChatApi;

public class ChatFactory {
	@Produces
	ChatApi create() {
		Writer writer = new OutputStreamWriter(System.out);
		return ToXmlEncoder.create(ChatApi.class, writer);
	}
}
