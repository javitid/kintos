package com.latarce.kintos14;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Novedades extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.novedades);
		TextView textView= (TextView) findViewById(R.id.novedades_content); 
		String texto = getIntent().getStringExtra("message");
		textView.setText(texto);	
	}
}
