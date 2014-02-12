package com.latarce.kintos14;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Chat extends Activity implements OnClickListener{

	private String LOADURL = "http://latarce.com/spl_news/chat.php?act=androidload";
	private String SENDURL = "http://latarce.com/spl_news/chat.php?act=insert";
	private ListView listView;
	private Button btSend;
	private EditText chatNewText;
	private EditText usuarioText;
	private RefreshChat refreshChat = null;
	private ClipboardManager clipboard;
	
	private Runnable activityRefresh, activityToSendMsg;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		btSend = (Button)this.findViewById(R.id.send);
		btSend.setOnClickListener(this);
		
		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		
		chatNewText = (EditText) findViewById(R.id.chatNewText);
		usuarioText = (EditText) findViewById(R.id.usuarioText);
		
		// ArrayList and ArrayAdapter
		listView = (ListView) findViewById(R.id.msgView);
		
		// Remove focus from edit text box
		chatNewText.clearFocus();
		
		// Chat handler
		handler = new Handler();
		
		// Request data from server
		activityRefresh = new Runnable() {
			public void run() {
				refreshChat = new RefreshChat();
				refreshChat.execute();
				handler.postDelayed(activityRefresh, 10000);
			}
		};
		handler.post(activityRefresh);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Kill chat refresh threat
		handler.removeCallbacks(activityRefresh);
		handler.removeCallbacks(activityToSendMsg);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.send:
				//Toast.makeText(getBaseContext(), getResources().getString(R.string.chat_no_disponible), Toast.LENGTH_LONG).show();
				if (chatNewText.getText().toString().matches(""))
					Toast.makeText(getBaseContext(), getResources().getString(R.string.chat_introduce_mensaje), Toast.LENGTH_LONG).show();
				else{					
					// Request data from server
					activityToSendMsg = new Runnable() {
						public void run() {
							SendSmsToChat sendSmsToChat = new SendSmsToChat();
							sendSmsToChat.execute();
						}
					};
					
					activityRefresh = new Runnable() {
						public void run() {
							refreshChat = new RefreshChat();
							refreshChat.execute();
						}
					};
					
					handler.post(activityToSendMsg);
					handler.post(activityRefresh);
				}
				break;
		}
	}
	
	
	public class RefreshChat extends AsyncTask<Void, Void, Boolean> {
		private RestClient clientResponse;
		@Override
		protected Boolean doInBackground(Void... params) {
			clientResponse = loadServerMsgs();
			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			printMsgs(clientResponse);
			refreshChat = null;
		}
		@Override
		protected void onCancelled() {
			refreshChat = null;
		}
	}

	
	public class SendSmsToChat extends AsyncTask<Void, Void, Boolean> {
		private RestClient clientResponse;
		@Override
		protected Boolean doInBackground(Void... params) {
			clientResponse = sendMsg();
			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			chatNewText.setText("");
			printMsgs(clientResponse);
			refreshChat = null;
		}
		@Override
		protected void onCancelled() {
			refreshChat = null;
		}
	}
	
	
    // Load Server Messages - REST Service
    public RestClient loadServerMsgs(){  
	    RestClient client = new RestClient(LOADURL);
	    //adding parameter
	    //client.AddParam("param 1 name", "param 1 value");
	    //adding header to the request
	    //client.AddHeader("header 1 name", "header 1 value");
	     
	    try {
	        //the actual call here
	        client.Execute(RestClient.RequestMethod.GET);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return client;		
    }
    
    
    // Send Messages - REST Service
    public RestClient sendMsg(){  	
    	String nick = usuarioText.getText().toString().replaceAll(" ", "%20");
    	if (nick.matches("")){
    		nick = "Invitado";
    	}
    	String completeURL = SENDURL + "&user=" + nick + "&msg=" + chatNewText.getText().toString().replaceAll(" ", "%20");
	    RestClient client = new RestClient(completeURL);

	    try {
	        //the actual call here
	        client.Execute(RestClient.RequestMethod.GET);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return client;
    }
    
    
    // Print messages
    public void printMsgs(RestClient client){
	    // Setting messages into Layout
        // Create the grid item mapping
		String[] from = new String[] {"usuario", "fecha", "mensaje"};
		int[] to = new int[] {R.id.usuario, R.id.fecha, R.id.mensaje};
		
		// Lists declaration
		List<String> fecha = new ArrayList<String>();
		List<String> usuario = new ArrayList<String>();
		List<String> mensaje = new ArrayList<String>();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		
	    //retrieving the response of the call
	    //int responseCode = client.getResponseCode();
	    String response = client.getResponse();
	    
	    // Check if the connection fails
	    if (response != null){
			// Add messages from JSON response
		    try {
		    	JSONObject jsonMessages = new JSONObject(response);
		    	JSONArray jsonArray = jsonMessages.getJSONArray("mensajes");
		    	for (int i = 0; i < jsonArray.length(); i++) {
			        JSONObject jsonObject = jsonArray.getJSONObject(i);
			        usuario.add(jsonObject.getString("usuario"));
			        fecha.add(jsonObject.getString("fecha"));
					mensaje.add(jsonObject.getString("mensaje"));
		    	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Add each String,String element to a new HashMap
			for (int j=0; j<fecha.size(); j++) {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("usuario", usuario.get(j));
				map.put("fecha", fecha.get(j));
				map.put("mensaje", mensaje.get(j));
				list.add(map);
			}

			SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.chat_list, from, to);        
			listView.setAdapter(adapter);
			listView.setCacheColorHint(0);
			listView.setStackFromBottom(true);
			
			listView.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					String copyText = ((TextView)arg1.findViewById(R.id.mensaje)).getText().toString();
					clipboard.setText(copyText);
					Toast.makeText(getBaseContext(), getResources().getString(R.string.chat_texto_copiado) + " " + copyText ,Toast.LENGTH_LONG).show();
				}
	        });
	    }
	    else{
	    	Toast.makeText(getBaseContext() , getResources().getString(R.string.chat_sin_conexion), Toast.LENGTH_LONG).show();
	    }
    }
    
}
