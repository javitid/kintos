package com.latarce.kintos14;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Calendar extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		
		TextView daysLeftText= (TextView)this.findViewById(R.id.daysLeft);
		CalendarView calendar = (CalendarView)this.findViewById(R.id.calendarView1);
		
		Time now = new Time();
		now.setToNow();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		SimpleDateFormat daysLeftFormatter = new SimpleDateFormat("dd");
		SimpleDateFormat hoursLeftFormatter = new SimpleDateFormat("HH");
		long startDate=0, actualDate=0, daysLeft=0;
		try {
			startDate = formatter.parse("07-03-2014 22:00").getTime();
			actualDate = now.toMillis(true);
			daysLeft = startDate - actualDate;
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		
		calendar.setShowWeekNumber(false);
		try {
			calendar.setMinDate(formatter.parse("01-01-2014 00:01").getTime());
			calendar.setDate(actualDate, true, true);
			calendar.setMaxDate(formatter.parse("31-12-2014 23:59").getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Date dateLeft = new Date(daysLeft);
		daysLeftText.setText("¡Faltan " + String.valueOf(Integer.parseInt(daysLeftFormatter.format(dateLeft)) - 1) + " días y " + Integer.parseInt(hoursLeftFormatter.format(dateLeft)) + " horas para Quintos!");
		
		calendar.setOnDateChangeListener(new OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                    int dayOfMonth) {
            	if (year==2014){
	            	if (month==2){
		            	if (dayOfMonth==7){
		            		Toast.makeText(getApplicationContext(), "Día " + dayOfMonth + ", ¡a las 22:00 empiezan los Quintos 2014!", Toast.LENGTH_LONG).show();
		            	}
		            	else if (dayOfMonth == 8){
		            		Toast.makeText(getApplicationContext(), "Día " + dayOfMonth + ", ¡el día de la cena!", Toast.LENGTH_LONG).show();
		            	}
		            	else if (dayOfMonth == 9){
		            		Toast.makeText(getApplicationContext(), "Día " + dayOfMonth + ", ¡por la mañana a correr el bollo, por la tarde a la carrera de cintas y por la noche al baile de fin de fiestas!", Toast.LENGTH_LONG).show();
		            	}
		            	else if (dayOfMonth < 7){
		            		Toast.makeText(getApplicationContext(), "Día " + dayOfMonth + ", ¡los Quintos empiezan el día 7!", Toast.LENGTH_SHORT).show();
		            	}
		            	else if (dayOfMonth > 9){
		            		Toast.makeText(getApplicationContext(), "Día " + dayOfMonth + ", Toca esperar a los Quintos del año que viene...", Toast.LENGTH_SHORT).show();
		            	}
	            	}
	            	else if (month<2){
	            		Toast.makeText(getApplicationContext(), "¡Los Quintos empiezan el día 7 de Marzo!", Toast.LENGTH_SHORT).show();
	            	}
	            	else if (month>2){
	            		Toast.makeText(getApplicationContext(), "Toca esperar a los Quintos del año que viene...", Toast.LENGTH_SHORT).show();
	            	}
            	}
            }
        });
	}

}
