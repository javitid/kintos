package com.latarce.kintos14;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Programa extends Activity{
	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programa);
		
		webview = (WebView) this.findViewById(R.id.webView);
		webview.setInitialScale(100);
	    webview.getSettings().setSupportZoom(true);
	    webview.getSettings().setBuiltInZoomControls(true);
	    webview.loadData("En esta seccion ira una imagen con el cartel", "text/html", null);
	}
}
