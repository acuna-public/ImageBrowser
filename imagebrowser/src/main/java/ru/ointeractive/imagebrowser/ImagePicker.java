	package ru.ointeractive.imagebrowser;
	/*
	 Created by Acuna on 10.05.2018
	*/
	
	import android.app.Activity;
	import android.content.Intent;
  import android.os.Bundle;
  
  import upl.json.JSONArray;
  import upl.json.JSONException;
	import upl.json.JSONObject;
  
  import upl.util.ArrayList;
  
	import ru.ointeractive.andromeda.OS;
  import ru.ointeractive.jabadaba.Arrays;
  import ru.ointeractive.jabadaba.Int;
	
	public class ImagePicker {
		
		private final Activity activity;
		private Intent intent;
		
		public static final String EXTRA_SELECTED_IMAGES = "selections";
    public static final String EXTRA_SUBMIT_DRAGNDROP = "submit_dragndrop";
    public static final String EXTRA_AUX_DRAGNDROP = "aux_dragndrop";
    public static final String EXTRA_PROGRESS = "progress";
		
		public ImagePicker (Activity activity) {
			
			this.activity = activity;
			this.intent = new Intent (activity, ImagesActivity.class);
			
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
		
    public ImagePicker setStorageType (String type) {
  			
      intent.putExtra ("storage_type", type);
      return this;
      
    }
    
    public ImagePicker setStorageData (JSONObject data) throws JSONException {
    
		  JSONArray keys = data.names ();
		  
		  for (int i = 0; i < upl.core.Int.size (keys); ++i) {
		    
		    String key = keys.getString (i);
		    Object value = data.get (key);
		    
        if (value instanceof Integer)
          intent.putExtra (key, (int) value);
		    else
          intent.putExtra (key, value.toString ());
		    
      }
      
      return this;
		  
    }
    
		public ImagePicker setImageSize (int size) {
			
			intent.putExtra ("image_size", size);
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
			
			intent = OS.toIntent (data, intent);
			
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
    
    public ImagePicker setSubmitDragNDrop (boolean dragnDrop) {
      
      intent.putExtra (EXTRA_SUBMIT_DRAGNDROP, dragnDrop);
      return this;
      
    }
    
    public ImagePicker setProgress (boolean set) {
    
		  intent.putExtra (EXTRA_PROGRESS, set);
		  return this;
		  
    }
    
    public ImagePicker start () {
			
			activity.startActivityForResult (intent, 200);
			
			return this;
			
		}
		
		public static boolean isResult (int requestCode, int resultCode, Intent data) {
			return (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null);
		}
		
		public static java.util.ArrayList<String> getImagesList (Intent data) { // Needed ArrayList (not List) to put it to Intent (if needed)
			
			java.util.ArrayList<String> images = data.getStringArrayListExtra (EXTRA_SELECTED_IMAGES);
			Arrays.rsort (images);
			
			return images;
			
		}
		
    public static java.util.ArrayList<String> getImagesList (Bundle data) { // Needed ArrayList (not List) to put it to Intent (if needed)
      return data.getStringArrayList (EXTRA_SELECTED_IMAGES);
    }
		
	}