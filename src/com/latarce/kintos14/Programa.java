package com.latarce.kintos14;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Programa extends Activity{
	private TextView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.programa);
		
		view = (TextView) this.findViewById(R.id.cartelView);
	    view.setBackgroundResource(R.drawable.cartel13);
	}
}
