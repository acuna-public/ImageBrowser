  package ru.ointeractive.imagebrowser;
  
  import android.graphics.Bitmap;
  import android.os.AsyncTask;
  import android.support.annotation.NonNull;
  import android.support.v7.widget.GridLayoutManager;
  import android.support.v7.widget.RecyclerView;
  import android.support.v7.widget.helper.ItemTouchHelper;
  import android.view.View;
  
  import java.io.IOException;
  import upl.util.ArrayList;
  import java.util.Collections;
  import upl.util.List;
  
  import ru.ointeractive.androdesign.widget.GridView;
  import ru.ointeractive.androdesign.widget.NestedScrollView;
  import ru.ointeractive.androdesign.widget.ScrollView;
  import ru.ointeractive.filedialog.Provider;
  import ru.ointeractive.jabadaba.Int;
  import ru.ointeractive.jabadaba.Log;
  import upl.core.exceptions.HttpRequestException;
  import upl.core.exceptions.OutOfMemoryException;
  import ru.ointeractive.jstorage.StorageException;
  
  class ShowItems extends AsyncTask<Void, Integer, List<Provider>> {
    
    private final ImageBrowser imageBrowser;
    private List<Exception> errors = new ArrayList<> (), outErrors = new ArrayList<> ();
    
    private ImageBrowser.LoaderListener lListener;
    
    ShowItems (ImageBrowser imageBrowser, ImageBrowser.LoaderListener lListener) {
      
      this.imageBrowser = imageBrowser;
      this.lListener = lListener;
	    
    }
    
    private Bitmap getImage (ru.ointeractive.jstorage.Item item) throws StorageException, HttpRequestException, OutOfMemoryException, IOException {
      
      for (ru.ointeractive.jstorage.Item file : item.thumbsList ()) {
      	
	      if (!file.isDir)
		      return imageBrowser.provider.newInstance (file).getImage (); // Сканим до первой картинки
	      
      }
      
      return null;
      
    }
    
    @Override
    public List<Provider> doInBackground (Void... params) {
      
      try {
        
        imageBrowser.provider.item.storage.pagination.offset = (imageBrowser.provider.item.storage.pagination.page * imageBrowser.provider.item.storage.pagination.perPage);
				
	      //imageBrowser.files = new ArrayList<> (); // TODO ???
       
	      for (Provider provider : imageBrowser.provider.list ()) {
          
          ImageBrowser.Item item = new ImageBrowser.Item (provider);
          
          try {
	          
            if (provider.item.isDir) {
              
              item.image = getImage (provider.item);
              item.title = provider.folderTitle ();
              
            } else item.image = provider.getImage ();
	          
          } catch (IOException | HttpRequestException | StorageException e) {
            item.errors.add (e);
          } catch (OutOfMemoryException e) {
            item.outErrors.add (e);
          }
          
          imageBrowser.files.add (item);
          
        }
        
        imageBrowser.provider.item.storage.pagination.page++;
        
      } catch (StorageException e) {
        errors.add (e);
      } catch (OutOfMemoryException e) {
        outErrors.add (e);
      }
      
      return null;
      
    }
    
    @Override
    public void onPostExecute (final List<Provider> files2) {
      
      if (imageBrowser.gridView == null && imageBrowser.recyclerView == null)
        imageBrowser.gridView = imageBrowser.activity.findViewById (R.id.grid_view);
      
      if (imageBrowser.nestedScrollView == null && imageBrowser.scrollView == null)
        imageBrowser.nestedScrollView = imageBrowser.activity.findViewById (R.id.scroll_view);
      
      if (imageBrowser.imageLayout == 0) imageBrowser.imageLayout = R.layout.item_image;
      
      if (imageBrowser.gridView != null) {
        
        if (imageBrowser.gridAdapter == null) {
          
          imageBrowser.gridAdapter = new ImagesGridAdapter (imageBrowser.imageLayout, imageBrowser.files, imageBrowser.imageSize);
          
          imageBrowser.gridAdapter.setListener (new ImagesGridAdapter.Listener () {
            
            @Override
            public void onView (ImagesGridAdapter.ViewHolder holder) {
              
              //ViewGroup.LayoutParams params = imageView.getLayoutParams ();
              
              //int width = imageView.getMeasuredWidth ();
              
              //params.width = width;
              //params.height = width;
              
            }
            
            @Override
            public void onItemClick (ImagesGridAdapter.ViewHolder holder, ImageBrowser.Item item) {
              
              if (imageBrowser.listener != null)
                imageBrowser.listener.onItemClick (holder, item);
              else if (imageBrowser.gridListener != null)
                imageBrowser.gridListener.onItemClick (holder, item);
              
            }
            
            @Override
            public boolean onItemLongClick (ImagesGridAdapter.ViewHolder holder, ImageBrowser.Item item) {
              
              return (
                (imageBrowser.listener != null && imageBrowser.listener.onItemLongClick (holder, item))
                  ||
                  (imageBrowser.gridListener != null && imageBrowser.gridListener.onItemLongClick (holder, item))
              );
              
            }
            
          });
          
          imageBrowser.gridView.setVisibility (View.VISIBLE);
          imageBrowser.gridView.setColumnWidth (imageBrowser.imageSize);
          
          imageBrowser.gridView.setAdapter (imageBrowser.gridAdapter);
          
        } else imageBrowser.gridAdapter.notifyDataSetChanged ();
        
      } else if (imageBrowser.recyclerView != null || imageBrowser.optDragNDrop) {
        
        if (imageBrowser.recyclerAdapter == null) {
          
          imageBrowser.recyclerAdapter = new ImagesRecyclerAdapter (imageBrowser.activity, imageBrowser.imageLayout, imageBrowser.files, imageBrowser.imageSize);
          
          imageBrowser.recyclerAdapter.setListener (new ImagesRecyclerAdapter.Listener () {
            
            @Override
            public void onView (ImagesRecyclerAdapter.ViewHolder holder) {
              
              //ViewGroup.LayoutParams params = imageView.getLayoutParams ();
              
              //int width = imageView.getMeasuredWidth ();
              
              //params.width = width;
              //params.height = width;
              
            }
            
            @Override
            public void onItemClick (ImagesRecyclerAdapter.ViewHolder holder, ImageBrowser.Item item) {
	            
              if (imageBrowser.listener != null)
                imageBrowser.listener.onItemClick (holder, item);
              else if (imageBrowser.recyclerListener != null)
                imageBrowser.recyclerListener.onItemClick (holder, item);
              
            }
            
            @Override
            public boolean onItemLongClick (ImagesRecyclerAdapter.ViewHolder holder, ImageBrowser.Item item) {
              
              return (
                (imageBrowser.listener != null && imageBrowser.listener.onItemLongClick (holder, item))
                  ||
                  (imageBrowser.recyclerListener != null && imageBrowser.recyclerListener.onItemLongClick (holder, item))
              );
              
            }
            
          });
          
          imageBrowser.recyclerView.setLayoutManager (new GridLayoutManager (imageBrowser.activity, 2));
          
          imageBrowser.recyclerView.setAdapter (imageBrowser.recyclerAdapter);
          
        } else {
          
          int newPage = (imageBrowser.provider.item.storage.pagination.page * imageBrowser.provider.item.storage.pagination.perPage);
          
          imageBrowser.recyclerAdapter.notifyItemInserted (newPage);
          imageBrowser.recyclerAdapter.notifyItemRangeChanged (newPage, (newPage * 2));
          
        }
        
      }
      
      if (imageBrowser.scrollView != null) {
        
        imageBrowser.scrollView.setListener (new ScrollView.OnScrollListener () {
          
          @Override
          public void onScrollBottomEnd (View view) {
            
            if (imageBrowser.listener != null)
              imageBrowser.listener.onScrollEnd (imageBrowser.provider.item);
            else if (imageBrowser.gridListener != null)
              imageBrowser.gridListener.onScrollEnd (imageBrowser.provider.item);
            else if (imageBrowser.recyclerListener != null)
              imageBrowser.recyclerListener.onScrollEnd (imageBrowser.provider.item);
            
          }
          
        });
        
      } else if (imageBrowser.nestedScrollView != null) {
        
        imageBrowser.nestedScrollView.setListener (new NestedScrollView.OnScrollListener () {
          
          @Override
          public void onScrollBottomEnd (View view) {
            
            if (imageBrowser.listener != null)
              imageBrowser.listener.onScrollEnd (imageBrowser.provider.item);
            else if (imageBrowser.gridListener != null)
              imageBrowser.gridListener.onScrollEnd (imageBrowser.provider.item);
            else if (imageBrowser.recyclerListener != null)
              imageBrowser.recyclerListener.onScrollEnd (imageBrowser.provider.item);
            
          }
          
        });
        
      } else if (imageBrowser.gridView != null) {
        
        imageBrowser.gridView.setListener (new GridView.OnScrollListener () {
          
          @Override
          public void onScrollBottomEnd (View view, int scrollState) {
            
            if (imageBrowser.listener != null)
              imageBrowser.listener.onScrollEnd (imageBrowser.provider.item);
            else if (imageBrowser.gridListener != null)
              imageBrowser.gridListener.onScrollEnd (imageBrowser.provider.item);
            else if (imageBrowser.recyclerListener != null)
              imageBrowser.recyclerListener.onScrollEnd (imageBrowser.provider.item);
            
          }
          
        });
        
      }
      
      if (imageBrowser.listener != null) {
        
        if (Int.size (errors) > 0)
          for (Exception e : errors)
            imageBrowser.listener.onError (e);
        
        if (Int.size (outErrors) > 0)
          for (Exception e : outErrors)
            imageBrowser.listener.onOutOfMemoryError (e);
        
      } else if (imageBrowser.gridListener != null) {
        
        if (Int.size (errors) > 0)
          for (Exception e : errors)
            imageBrowser.gridListener.onError (e);
        
        if (Int.size (outErrors) > 0)
          for (Exception e : outErrors)
            imageBrowser.gridListener.onOutOfMemoryError (e);
        
      } else if (imageBrowser.recyclerListener != null) {
        
        if (Int.size (errors) > 0)
          for (Exception e : errors)
            imageBrowser.recyclerListener.onError (e);
        
        if (Int.size (outErrors) > 0)
          for (Exception e : outErrors)
            imageBrowser.recyclerListener.onOutOfMemoryError (e);
        
      }
      
      if (imageBrowser.recyclerView != null && imageBrowser.optDragNDrop)
        new ItemTouchHelper (new ItemTouchHelper.Callback () {
          
          @Override
          public boolean onMove (@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            
            int fromPosition = viewHolder.getAdapterPosition ();
            int toPosition = target.getAdapterPosition ();
            
            /*if (fromPosition < toPosition) {
              
              for (int i = fromPosition; i < toPosition; i++)
                Collections.swap (files, i, i + 1);
              
            } else {
              
              for (int i = fromPosition; i > toPosition; i--)
                Collections.swap (files, i, i - 1);
              
            }*/
            
            //recyclerAdapter.isUpdated (true);
            
            Collections.swap (imageBrowser.files, fromPosition, toPosition);
            
            imageBrowser.recyclerAdapter.notifyItemMoved (fromPosition, toPosition);
            
            return (
              (imageBrowser.listener != null && imageBrowser.listener.onMove (imageBrowser.files, fromPosition, toPosition))
                ||
                (imageBrowser.recyclerListener != null && imageBrowser.recyclerListener.onMove (imageBrowser.files, fromPosition, toPosition))
            );
            
          }
          
          @Override
          public void onSwiped (@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            imageBrowser.recyclerAdapter.notifyDataSetChanged ();
          }
          
          @Override
          public int getMovementFlags (@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return makeMovementFlags (ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
          }
          
          @Override
          public boolean isItemViewSwipeEnabled () {
            return true;
          }
          
        }).attachToRecyclerView (imageBrowser.recyclerView);
	    
      if (lListener != null) lListener.onLoad (imageBrowser.provider.item);
      
      if (imageBrowser.progress != null) {
        
        imageBrowser.progress.dismiss ();
        imageBrowser.mProg = false;
        
      }
      
    }
    
  }