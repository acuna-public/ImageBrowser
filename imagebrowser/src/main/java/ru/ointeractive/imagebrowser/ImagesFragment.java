  package ru.ointeractive.imagebrowser;
  
  import android.app.Activity;
  import android.content.Intent;
  import android.os.Bundle;
  import android.support.annotation.NonNull;
  import android.support.v4.app.Fragment;
  import android.support.v4.content.ContextCompat;
  import android.support.v7.app.AppCompatActivity;
  import android.view.LayoutInflater;
  import android.view.MenuItem;
  import android.view.View;
  import android.view.ViewGroup;
  
  import upl.json.JSONException;
  import upl.json.JSONObject;
	
  import java.util.ArrayList;
  
  import ru.ointeractive.andromeda.OS;
  import ru.ointeractive.filedialog.FileDialog;
  import ru.ointeractive.jabadaba.Arrays;
  import ru.ointeractive.jabadaba.Files;
  import ru.ointeractive.jabadaba.Int;
  import ru.ointeractive.jabadaba.Log;
  import ru.ointeractive.jstorage.Item;
  import ru.ointeractive.storage.Storage;
  import ru.ointeractive.jstorage.StorageException;
	
  public class ImagesFragment extends Fragment {
    
    private ArrayList<String> selItems = new ArrayList<> (), nav = new ArrayList<> (), titles = new ArrayList<> ();
    private String descr, currentDir;
    private int screen = 0, minNum, maxNum;
    
    private Storage storage;
    private ImagesActivity activity;
    private ImageBrowser browser;
    
    private boolean optSubmitDragNDrop = false, optAuxDragNDrop = false;
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
      return inflater.inflate (getArguments ().getInt ("layout"), parent, false);
    }
    
    @Override
    public void onViewCreated (@NonNull View view, Bundle savedInstanceState) {
      
      selItems = ImagePicker.getImagesList (getArguments ());
      nav = getArguments ().getStringArrayList ("nav");
	    titles = getArguments ().getStringArrayList ("titles");
      currentDir = getArguments ().getString ("root_dir", "");
      optSubmitDragNDrop = getArguments ().getBoolean (ImagePicker.EXTRA_SUBMIT_DRAGNDROP, optSubmitDragNDrop);
      optAuxDragNDrop = getArguments ().getBoolean (ImagePicker.EXTRA_AUX_DRAGNDROP, optAuxDragNDrop);
      minNum = getArguments ().getInt ("min_num", 0);
      maxNum = getArguments ().getInt ("max_num", 0);
      descr = getArguments ().getString ("toolbar_descr", "");
      
      String storageType = getArguments ().getString ("storage_type");
      
      nav.add (currentDir);
	    
	    activity = (ImagesActivity) getActivity ();
      
      activity.toolbar.setTitle ((descr.equals ("") ? getString (R.string.title_multi) : descr));
      
      try {
        
        storage = new Storage (activity);
        
        if (storageType == null) {
        	
	        JSONObject data = new JSONObject ();
	        
	        data.put ("folder", activity.andro.getExternalFilesDir ());
          
          JSONObject data2 = new JSONObject ();
          data2.put (activity.andro.sdcard, data);
	        
          storage.setConfigs (data2);
          storage.getProvider (activity.andro.sdcard);
          
        } else {
        	
	        storage.setConfigs (getArguments ().getString ("storage_type"), getArguments ());
	        storage.getProvider (storageType);
	        
        }
        
        browser = new ImageBrowser (activity).setImageWidth (getArguments ().getInt ("image_size", 300));
        
        browser.setListener (new ImageBrowser.Listener () {
          
          @Override
          public void onLoad (Item item) {}
          
          @Override
          public void onItemClick (Object holder, ImageBrowser.Item item) {
            
            currentDir = item.getFile ();
            Log.w (111, currentDir);
            
            try {
	
	            if (item.provider.item.isDir) {
		
		            ++screen;
		
		            nav.add (currentDir);
		            titles.add (item.provider.item.getName ());
		
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
			
			            if (Int.size (selItems) > 0)
				            activity.fab.setVisibility (View.VISIBLE);
			            else
				            activity.fab.setVisibility (View.GONE);
			
			            if (getArguments ().getInt ("type", FileDialog.SelectionType.MULTIPLE.ordinal ()) == FileDialog.SelectionType.MULTIPLE.ordinal ()) {
				
				            if (holder instanceof ImagesGridAdapter.ViewHolder) {
					
					            ImagesGridAdapter.ViewHolder holder2 = (ImagesGridAdapter.ViewHolder) holder;
					
					            holder2.gridLayout.setForeground (isSelected ? ContextCompat.getDrawable (activity, R.drawable.ic_done_white_24dp) : null);
					
					            holder2.alphaView.setVisibility (View.VISIBLE);
					            holder2.alphaView.setAlpha (isSelected ? 0.6f : 0f);
					
				            } else {
					
					            ImagesRecyclerAdapter.ViewHolder holder2 = (ImagesRecyclerAdapter.ViewHolder) holder;
					
					            holder2.layout.setForeground (isSelected ? ContextCompat.getDrawable (activity, R.drawable.ic_done_white_24dp) : null);
					
					            holder2.alphaView.setVisibility (View.VISIBLE);
					            holder2.alphaView.setAlpha (isSelected ? 0.6f : 0f);
					
				            }
				
				            activity.invalidateOptionsMenu ();
				
			            } else submit ();
			
		            }
		
	            }
	            
            } catch (StorageException e) {
	            OS.alert (activity, e);
            }
            
          }
          
          @Override
          public boolean onItemLongClick (Object holder, ImageBrowser.Item item) {
            
            /*try {
              
              if (!item.provider.isDir ()) {
                
                Intent intent = new Intent (activity, ImageActivity.class);
                
                intent.putExtra ("image", item.provider.getDirectLink ());
                
                startActivity (intent);
                
              }
              
            } catch (StorageException | OutOfMemoryException e) {
              OS.alert (activity, e);
            }*/ // TODO Need AsyncTask
            
            return true;
            
          }
          
          @Override
          public void onScrollEnd (Item item) {
            
            if (!optAuxDragNDrop) {
              
              Images provider = (Images) browser.provider.setItem (item);
              browser.setProvider (provider);
              
            }
            
          }
          
          @Override
          public boolean onMove (upl.util.List<ImageBrowser.Item> files, int fromPosition, int toPosition) {
            
            selItems = new ArrayList<> ();
            
            for (ImageBrowser.Item file : files)
              selItems.add (file.getFile ());
            
            return true;
            
          }
          
          @Override
          public void onError (Exception e) {
            OS.alert (activity, e);
          }
          
          @Override
          public void onOutOfMemoryError (Exception e) {
            OS.alert (activity, e);
          }
          
        });
        
        browser.setProgress (getArguments ().getBoolean (ImagePicker.EXTRA_PROGRESS, false));
        
        browser.setScrollView (view.findViewById (R.id.scroll_view));
        
        browser.setSubmitDragNDrop (optSubmitDragNDrop);
        
        if (optSubmitDragNDrop && Int.size (selItems) > 0) { // Картинки уже получены
          
          browser.setView (view.findViewById (R.id.recycler_view));
          browser.clear ().setProvider (((Images) new Images (browser, storage.getItem ()).setFiles (selItems)));
          
        } else {
          
          browser.setView (view.findViewById (R.id.grid_view));
          
          newPage ();
          
        }
        
        activity.fab.setOnClickListener (new View.OnClickListener () {
          
          @Override
          public void onClick (View view) {
            
            if (minNum == 0 || Int.size (selItems) >= minNum) {
              
              if (maxNum == 0 || Int.size (selItems) <= maxNum)
                submit ();
              else if (!getArguments ().getString ("max_num_mess", "").equals (""))
                OS.alert (activity, getArguments ().getString ("max_num_mess"));
              
            } else if (!getArguments ().getString ("min_num_mess", "").equals (""))
              OS.alert (activity, getArguments ().getString ("min_num_mess"));
            
          }
          
        });
        
      } catch (JSONException | StorageException e) {
        OS.alert (activity, e);
      }
      
    }
    
    private void newPage () throws StorageException {
      
      String path = nav.get (screen);
      
      activity.toolbar.setDescription (Int.size (titles) > 1 ? Arrays.implode (Files.DS, titles) : descr);
      browser.clear ().setProvider (new Images (browser, storage.getItem (path).isDir (true)));
      
    }
    
    void submit () {
      
      if (optSubmitDragNDrop && !optAuxDragNDrop) {
        
        activity.toolbar.setTitle (R.string.title_dragndrop);
        
        newFragment (activity, getArguments (), R.layout.content_images_recycler, true);
        
      } else {
        
        Intent intent = new Intent ();
        
        intent.putExtra (ImagePicker.EXTRA_SELECTED_IMAGES, selItems);
        
        activity.setResult (Activity.RESULT_OK, intent);
        
        activity.finish ();
        
      }
      
    }
    
    public void newFragment (AppCompatActivity activity, Bundle bundle, int layout, boolean dragNDrop) {
      
      Fragment fragment = new ImagesFragment ();
      
      bundle.putInt ("layout", layout);
      bundle.putStringArrayList (ImagePicker.EXTRA_SELECTED_IMAGES, selItems);
      bundle.putBoolean (ImagePicker.EXTRA_AUX_DRAGNDROP, dragNDrop);
	    bundle.putStringArrayList ("nav", nav);
	    bundle.putStringArrayList ("titles", titles);
	    
	    fragment.setArguments (bundle);
      
      activity.getSupportFragmentManager ().beginTransaction ().replace (R.id.fragment, fragment).commit ();
      
    }
    
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
      
      super.onOptionsItemSelected (item);
      
      if (item.getItemId () == android.R.id.home && screen > 0) {
        
        nav.remove (screen);
        --screen;
        
        try {
	        newPage ();
	
	        browser.cancelTask ();
	
        } catch (StorageException e) {
	        OS.alert (activity, e);
        }
        
      } else activity.finish ();
      
      return true;
      
    }
    
  }