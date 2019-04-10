	package pro.acuna.imagebrowser;
	/*
	 Created by Acuna on 10.05.2018
	*/
	
	import android.content.Intent;
	import android.os.Bundle;
	import android.support.v4.content.ContextCompat;
	import android.support.v7.app.AppCompatActivity;
	import android.view.Menu;
	import android.view.MenuItem;
	import android.view.View;
	import android.widget.FrameLayout;
	
	import org.json.JSONException;
	import org.json.JSONObject;
	
	import java.util.ArrayList;
	
	import pro.acuna.androdesign.Andromeda;
	import pro.acuna.androdesign.ImageActivity;
	import pro.acuna.androdesign.widget.Toolbar;
	import pro.acuna.andromeda.OS;
  
  import pro.acuna.filedialog.Provider;
  import pro.acuna.filedialog.Type;
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.storage.Storage;
	import pro.acuna.storage.StorageException;
	
	public class ImagesActivity extends AppCompatActivity {
		
		private Bundle bundle;
		private ArrayList<String> selItems = new ArrayList<> (), nav = new ArrayList<> ();
		private String currentDir;
		private int screen = 0, minNum, maxNum;
		private Storage storage;
		
		private Toolbar toolbar;
		private ImageBrowser browser;
		
		@Override
		protected void onCreate (Bundle savedInstanceState) {
			
			super.onCreate (savedInstanceState);
			
			Andromeda andro = new Andromeda (this, "").init (this);
			
			setContentView (R.layout.activity_images);
			
			bundle = getIntent ().getExtras ();
			
			toolbar = andro.toolbar ();
			
			toolbar.setTitle ((bundle.getString ("toolbar_title", "").equals ("") ? getString (R.string.title_multi) : bundle.getString ("toolbar_title")));
			
			toolbar.setHomeButton (true);
			
			currentDir = bundle.getString ("root_dir", "");
			nav.add (currentDir);
			
			try {
				
				storage = new Storage (this);
				
				JSONObject data = new JSONObject ();
				
				if (bundle.getString ("storage_type") == null) {
					
					data.put ("folder", andro.getExternalFilesDir ());
					
					JSONObject data2 = new JSONObject ();
					data2.put (andro.sdcard, data);
					
					storage.init (andro.sdcard, data2);
					
				} else storage.init (bundle.getString ("storage_type"), bundle);
				
				browser = new ImageBrowser (this);
				
				minNum = bundle.getInt ("min_num", 0);
				maxNum = bundle.getInt ("max_num", 0);
				
				browser.setListener (new ImageBrowser.Listener () {
					
					@Override
					public void onItemClick (View view, int id, Provider item) {
						
						currentDir = item.toString ();
						
						if (item.isDir ()) {
							
							++screen;
							nav.add (currentDir);
							
							newPage ();
							
						} else {
							
							if (maxNum == 0 || (Int.size (selItems) < maxNum)) {
								
								boolean isSelected = Arrays.contains (currentDir, selItems);
								
								if (isSelected) {
									
									selItems.remove (currentDir);
									isSelected = false;
									
								} else {
									
									selItems.add (currentDir);
									isSelected = true;
									
								}
								
								if (bundle.getInt ("type", Type.MULTIPLE.ordinal ()) == Type.MULTIPLE.ordinal ()) {
									
									FrameLayout layout = view.findViewById (R.id.grid_item);
									layout.setForeground (isSelected ? ContextCompat.getDrawable (getApplicationContext (), R.drawable.ic_done_white_24dp) : null);
									
									View alphaView = view.findViewById (R.id.alpha_view);
									
									int size = bundle.getInt ("size", 300);
									
									alphaView.setLayoutParams (new FrameLayout.LayoutParams (size, size));
									alphaView.setAlpha (isSelected ? 0.5f : 0f);
									
									invalidateOptionsMenu ();
									
								} else submit ();
								
							}
							
						}
						
					}
					
					@Override
					public boolean onItemLongClick (View view, int id, Provider item) {
						
						if (!item.isDir ()) {
							
							try {
								
								Intent intent = new Intent (ImagesActivity.this, ImageActivity.class);
								
								intent.putExtra ("image", item.getDirectLink ());
								
								startActivity (intent);
								
							} catch (StorageException e) {
								OS.alert (getApplicationContext (), e);
							}
							
						}
						
						return true;
						
					}
					
					@Override
					public void onScrollEnd (Provider provider) {}
					
					@Override
					public void onError (Exception e) {
						OS.alert (getApplicationContext (), e);
					}
					
					@Override
					public void onOutOfMemoryError (Exception e) {
						OS.alert (getApplicationContext (), e);
					}
					
				});
				
				newPage ();
				
			} catch (JSONException | StorageException e) {
				OS.alert (getApplicationContext (), e);
			}
			
		}
		
		private void newPage () {
			
			String path = nav.get (screen);
			
			toolbar.setDescription (((bundle.getString ("toolbar_descr", "").equals ("") || Int.size (nav) > 1) ? path : bundle.getString ("toolbar_descr")));
			
			browser.clear ();
			browser.setProvider (new Images (browser, storage.toItem (path).isDir (true))).start ();
			
		}
		
		private void submit () {
			
			Intent intent = OS.implode (new Intent (), getIntent ());
			
			intent.putExtra (ImagePicker.EXTRA_SELECTED_IMAGES, selItems);
			
			setResult (RESULT_OK, intent);
			
			finish ();
			
		}
		
		private MenuItem doneButton;
		
		@Override
		public boolean onCreateOptionsMenu (Menu menu) {
			
			getMenuInflater ().inflate (R.menu.ip_main, menu);
			
			doneButton = menu.findItem (R.id.ip_action_done);
			doneButton.setVisible (false);
			
			return true;
			
		}
		
		@Override
		public boolean onPrepareOptionsMenu (Menu menu) {
			
			doneButton.setVisible (Int.size (selItems) > 0);
			
			return super.onPrepareOptionsMenu (menu);
			
		}
		
		@Override
		public boolean onOptionsItemSelected (MenuItem item) {
			
			if (item.getItemId () == android.R.id.home && screen > 0) {
				
				nav.remove (screen);
				--screen;
				
				newPage ();
				
			} else if (item.getItemId () == doneButton.getItemId ()) {
				
				if (minNum == 0 || Int.size (selItems) >= minNum) {
				  
				  if (Int.size (selItems) < maxNum)
            submit ();
          else if (!bundle.getString ("max_num_mess", "").equals (""))
            OS.alert (this, bundle.getString ("max_num_mess"));
          
        } else if (!bundle.getString ("min_num_mess", "").equals (""))
					OS.alert (this, bundle.getString ("min_num_mess"));
				
			} else finish ();
			
			return true;
			
		}
		
	}