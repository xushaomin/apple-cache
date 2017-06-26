package com.appleframework.cache.core.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class UrlSet extends HashSet<URL> {

	private static final long serialVersionUID = 1L;
	
	public void setUrls(String urls) {
		String[] urlss = urls.split(",");
		for (String key : urlss) {
			try {
				URL url = new URL(key);
				add(url);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
