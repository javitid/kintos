package com.latarce.kintos14;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An activity representing a list of Sections. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link SectionDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link SectionListFragment} and the item details (if present) is a
 * {@link SectionDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link SectionListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class SectionListActivity extends FragmentActivity implements
		SectionListFragment.Callbacks {

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	private CheckNovedades checkNovedades = null;
	private String LOADURL = "http://latarce.com/spl_news/check_novedades.php?action=get_novedades";
	private String USER = "user";
	private String PWD = "pwd";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_list);

		if (findViewById(R.id.section_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((SectionListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.section_list))
					.setActivateOnItemClick(true);
		}

		// Request data from server
		checkNovedades = new CheckNovedades();
		checkNovedades.execute();
	}

	/**
	 * Callback method from {@link SectionListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String idStr) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			//if (idStr.matches("1")){
			//	Programa fragment = new Programa();
			//	getSupportFragmentManager().beginTransaction().replace(R.id.section_detail_container, fragment).commit();
			//}

			Bundle arguments = new Bundle();
			arguments.putString(SectionDetailFragment.ARG_ITEM_ID, idStr);
			SectionDetailFragment fragment = new SectionDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.section_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent selectIntent = null;
			int id = Integer.parseInt(idStr);
			switch (id) {
				// BIENVENIDO
				case 1:
					selectIntent = new Intent(this, Programa.class);
					startActivity(selectIntent);
					break;

				// COMO LLEGAR
				case 2:
					selectIntent = new Intent(this, Gps.class);
					startActivity(selectIntent);
					break;

				// CALENDARIO
				case 3:
					if (android.os.Build.VERSION.SDK_INT >= 14){
						selectIntent = new Intent(this, Calendar.class);
						startActivity(selectIntent);
					}
					else{
						Toast.makeText(getBaseContext(), "El calendario solo está disponible para versiones de Android 4.0 o superiores", Toast.LENGTH_LONG).show();
					}
					break;
					
				// Chat
				case 4:
					selectIntent = new Intent(this, Chat.class);
					startActivity(selectIntent);
					break;
			
				// COMPARTIR
				case 5:
					selectIntent = new Intent(android.content.Intent.ACTION_SEND); 
					selectIntent.setType("text/plain");  
					selectIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
					String shareMessage = getResources().getString(R.string.share_message);  
					selectIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);  
					startActivity(Intent.createChooser(selectIntent, getResources().getString(R.string.share_chooser)));  
					break;
				
				// ACERCA DE
				case 6:
					selectIntent = new Intent(this, About.class);
					startActivity(selectIntent);
					break;
	
				default:
					selectIntent = new Intent(this, SectionDetailActivity.class);
					selectIntent.putExtra(SectionDetailFragment.ARG_ITEM_ID, idStr);
					startActivity(selectIntent);
					break;
				}
		}
	}
	
	// Async task
	public class CheckNovedades extends AsyncTask<Void, Void, Boolean> {
		private RestClient clientResponse;
		@Override
		protected Boolean doInBackground(Void... params) {
			clientResponse = loadServerMsgs();
			return true;
		}
		@Override
		protected void onPostExecute(final Boolean success) {
			printNovedadesIntent(clientResponse);
			checkNovedades = null;
		}
		@Override
		protected void onCancelled() {
			checkNovedades = null;
		}
	}
	
    // Load Server Messages - REST Service
    public RestClient loadServerMsgs(){  
	    RestClient client = new RestClient(LOADURL);
	    String authentication = USER + ":" + PWD;
    	String encoding = Base64.encodeToString(authentication.getBytes(), Base64.NO_WRAP);
    	client.AddHeader("Authorization", "Basic " + encoding);
	     
	    try {
	        //the actual call here
	        client.Execute(RestClient.RequestMethod.GET);
	    } catch (Exception e) {
	        e.printStackTrace();
	    } 
	    return client;		
    }
    
    // Get Status values
    public void printNovedadesIntent(RestClient client){
    	String response = client.getResponse();
    	if (response != null){
			// Add messages from JSON response
		    try {
		    	JSONObject jsonMessages = new JSONObject(response);
		    	JSONObject jsonStatus = jsonMessages.getJSONObject("novedades");	    	

		    	if (Integer.parseInt(jsonStatus.getString("quintos")) == 1){
		    		//Toast.makeText(getBaseContext(), "Novedades en Quintos", Toast.LENGTH_SHORT).show();
		    		Intent intent = new Intent(this, Novedades.class);
		    		intent.putExtra("message", jsonStatus.getString("mensaje_quintos"));
					startActivity(intent);
		    	}
		    	
		    	if (Integer.parseInt(jsonStatus.getString("latarce")) == 1){
		    		Toast.makeText(getBaseContext(), "Novedades en Latarce", Toast.LENGTH_LONG).show();
		    	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
    }
}
