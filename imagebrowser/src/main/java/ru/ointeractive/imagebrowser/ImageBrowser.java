  package ru.ointeractive.imagebrowser;
  /*
   Created by Acuna on 10.05.2018
  */
  
  import android.app.Activity;
  import android.app.ProgressDialog;
  import android.graphics.Bitmap;
  import android.os.AsyncTask;
  import android.support.v7.widget.RecyclerView;
  import android.view.View;

  import upl.util.ArrayList;
  import upl.util.List;
  
  import ru.ointeractive.androdesign.widget.GridView;
  import ru.ointeractive.androdesign.widget.NestedScrollView;
  import ru.ointeractive.androdesign.widget.ScrollView;
  import ru.ointeractive.filedialog.Provider;
  import ru.ointeractive.jabadaba.Log;

  public class ImageBrowser {
    
    Activity activity;
    int imageSize = 200;
    
    public Images provider;
    
    public ImageBrowser (Activity activity) {
      this.activity = activity;
    }
    
    public static class Item {
      
      public Bitmap image;
      public String title;
      
      List<Exception> errors = new ArrayList<> (), outErrors = new ArrayList<> ();
      
      public Provider provider;
      
      public Item (Provider provider) {
        this.provider = provider;
      }
      
      public String getFile () {
        return provider.item.getShortFile ();
      }
      
    }
    
    public interface Listener {
      
      void onLoad (ru.ointeractive.jstorage.Item item);
      void onItemClick (Object holder, Item item);
      boolean onItemLongClick (Object holder, Item item);
      void onScrollEnd (ru.ointeractive.jstorage.Item item);
      void onError (Exception e);
      void onOutOfMemoryError (Exception e);
      
      boolean onMove (List<Item> files, int oldIndex, int newIndex);
      
    }
    
    public interface GridListener {
      
      void onLoad (ru.ointeractive.jstorage.Item item);
      void onItemClick (ImagesGridAdapter.ViewHolder holder, Item item);
      boolean onItemLongClick (ImagesGridAdapter.ViewHolder holder, Item item);
      void onScrollEnd (ru.ointeractive.jstorage.Item item);
      void onError (Exception e);
      void onOutOfMemoryError (Exception e);
      
    }
    
    public interface RecyclerListener {
      
      void onLoad (ru.ointeractive.jstorage.Item item);
      void onItemClick (ImagesRecyclerAdapter.ViewHolder holder, Item item);
      boolean onItemLongClick (ImagesRecyclerAdapter.ViewHolder holder, Item item);
      void onScrollEnd (ru.ointeractive.jstorage.Item item);
      void onError (Exception e);
      void onOutOfMemoryError (Exception e);
      
      boolean onMove (List<Item> files, int oldIndex, int newIndex);
      
    }
    
    Listener listener;
    GridListener gridListener;
    RecyclerListener recyclerListener;
    
    public ImageBrowser setListener (Object listener) {
      
      if (listener instanceof Listener)
        this.listener = (Listener) listener;
      else if (listener instanceof GridListener)
        this.gridListener = (GridListener) listener;
      else if (listener instanceof RecyclerListener)
        this.recyclerListener = (RecyclerListener) listener;
      
      return this;
      
    }
    
    GridView gridView;
    RecyclerView recyclerView;
    
    public ImageBrowser setView (View view) {
      
      if (view instanceof GridView)
        this.gridView = (GridView) view;
      else if (view instanceof RecyclerView)
        this.recyclerView = (RecyclerView) view;
      
      return this;
      
    }
    
    ScrollView scrollView;
    NestedScrollView nestedScrollView;
    
    boolean optDragNDrop = false;
    
    public ImageBrowser setSubmitDragNDrop (boolean dragNDrop) {
      
      optDragNDrop = dragNDrop;
      return this;
      
    }
    
    public ImageBrowser setScrollView (View view) {
      
      if (view instanceof ScrollView)
        scrollView = (ScrollView) view;
      else if (view instanceof NestedScrollView)
        nestedScrollView = (NestedScrollView) view;
      
      return this;
      
    }
    
    public ImageBrowser setImageWidth (int size) {
      
      imageSize = size;
      return this;
      
    }
    
    public int getImageSize () {
      return imageSize;
    }
    
    int imageLayout = 0;
    
    public ImageBrowser setImageLayout (int layout) {
      
      imageLayout = layout;
      return this;
      
    }
    
    public interface LoaderListener {
      
      void onLoad (ru.ointeractive.jstorage.Item item);
      
    }
    
    public ImageBrowser setProvider (Images provider) {
      return setProvider (provider, null);
    }
    
    private AsyncTask<Void, Integer, List<Provider>> task;
    ProgressDialog progress;
    
    public ImageBrowser setProvider (Images provider, LoaderListener listener) {
      
      //java.lang.System.gc ();
      
      if (mProg) {
        
        progress = ProgressDialog.show (activity, null, activity.getString (R.string.loading));
        
        progress.setCancelable (false);
        progress.show ();
        
      }
      
      this.provider = provider;
      
      task = new ShowItems (this, listener).execute ();
      
      return this;
      
    }
    
    boolean prog = false, mProg = false;
    
    public ImageBrowser setProgress (boolean set) {
      
      prog = mProg = set;
      return this;
      
    }
    
    public void cancelTask () {
      if (task != null) task.cancel (true);
    }
    
    List<ImageBrowser.Item> files = new ArrayList<> ();
    
    ImagesGridAdapter gridAdapter;
    ImagesRecyclerAdapter recyclerAdapter;
    
    public ImageBrowser clear () {
      
      files = new ArrayList<> ();
      
      gridAdapter = null;
      recyclerAdapter = null;
      
      mProg = prog;
      
      if (provider != null) provider.item.storage.pagination.page = 0;
      
      cancelTask ();
      
      return this;
      
    }
    
  }