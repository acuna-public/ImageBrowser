	package pro.acuna.imagebrowser;
	/*
	 Created by Acuna on 10.05.2018
	*/
	
	import android.app.Activity;
	import android.content.Intent;
	
	import org.json.JSONException;
	import org.json.JSONObject;
	
	import java.util.List;
	
	import pro.acuna.andromeda.OS;
	
	public class ImagePicker {
		
		private Activity activity;
		private Intent intent;
		
		static final String EXTRA_SELECTED_IMAGES = "selections";
		
		public ImagePicker (Activity activity) {
			this (activity, new Intent (activity, ImagesActivity.class));
		}
		
		public ImagePicker (Activity activity, Intent intent) {
			
			this.activity = activity;
			this.intent = intent;
			
		}
		
		public ImagePicker setToolbarTitle (int title) {
			return setToolbarTitle (activity.getString (title));
		}
		
		public ImagePicker setToolbarTitle (String title) {
			
			intent.putExtra ("toolbar_title", title);
			return this;
			
		}
		
		public ImagePicker setToolbarDescr (int title) {
			return setToolbarDescr (activity.getString (title));
		}
		
		public ImagePicker setToolbarDescr (String title) {
			
			intent.putExtra ("toolbar_descr", title);
			return this;
			
		}
		
		public ImagePicker setRootDir (String path) {
			
			intent.putExtra ("root_dir", path);
			return this;
			
		}
		
		public ImagePicker setImageSize (int size) {
			
			intent.putExtra ("size", size);
			return this;
			
		}
		
		public ImagePicker setType (int type) {
			
			intent.putExtra ("type", type);
			return this;
			
		}
		
		public ImagePicker setMinNum (int limit) {
			
			intent.putExtra ("min_num", limit);
			return this;
			
		}
		
		public ImagePicker setMaxNum (int limit) {
			
			intent.putExtra ("max_num", limit);
			return this;
			
		}
		
		public ImagePicker setStorage (JSONObject data) throws JSONException {
			return setStorage (data.getString ("type"), data);
		}
		
		public ImagePicker setStorage (String type, JSONObject data) throws JSONException {
			
			intent.putExtra ("storage_type", type);
			
			this.intent = OS.toIntent (data, intent);
			
			return this;
			
		}
		
		public ImagePicker setMinNumMess (int mess) {
			return setMinNumMess (activity.getString (mess));
		}
		
		public ImagePicker setMinNumMess (String mess) {
			
			intent.putExtra ("min_num_mess", mess);
			return this;
			
		}
    
    public ImagePicker setMaxNumMess (int mess) {
      return setMinNumMess (activity.getString (mess));
    }
    
    public ImagePicker setMaxNumMess (String mess) {
      
      intent.putExtra ("max_num_mess", mess);
      return this;
      
    }
    
    public ImagePicker start () {
			
			activity.startActivityForResult (intent, 200);
			
			return this;
			
		}
		
		public static boolean isResult (int requestCode, int resultCode, Intent data) {
			return (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null);
		}
		
		public static List<String> getImagesList (Intent data) {
			return data.getStringArrayListExtra (EXTRA_SELECTED_IMAGES);
		}
		
	}