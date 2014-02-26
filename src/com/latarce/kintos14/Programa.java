package com.latarce.kintos14;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Programa extends Activity{
	private WebView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programa);
		
		view = (WebView) this.findViewById(R.id.cartelView);
	    //view.setBackgroundResource(R.drawable.cartel14a);
		String urlData = "<body><img src=\"cartel14a.jpg\"><br><br><img src=\"cartel14b.jpg\"><br><br>" +
				"<img src=\"cartel14c.jpg\"><br><br><img src=\"cartel14d.jpg\"><BR><BR><BR>Carteles creados por " +
				"<b>Rodrigo Canal Arribas - 630 11 68 41</b></body>";
		view.loadDataWithBaseURL("file:///android_asset/", urlData, "text/html", "utf-8", null);
		view.setInitialScale(120);
		view.getSettings().setSupportZoom(true);
		view.getSettings().setBuiltInZoomControls(true);
	}
}
