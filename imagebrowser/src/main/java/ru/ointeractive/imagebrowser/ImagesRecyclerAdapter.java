  package ru.ointeractive.imagebrowser;
  /*
   Created by Acuna on 19.12.2018
  */
  
  import android.app.Activity;
  import android.support.annotation.NonNull;
  import android.view.LayoutInflater;
  import android.view.View;
  import android.view.ViewGroup;
  import android.widget.FrameLayout;
  import android.widget.ProgressBar;
  import android.widget.TextView;
  
  import upl.util.List;
  
  import ru.ointeractive.androdesign.widget.ImageView;
  import ru.ointeractive.androdesign.widget.RecyclerView;
  import ru.ointeractive.jabadaba.Arrays;
  import ru.ointeractive.jabadaba.Int;
  
  public class ImagesRecyclerAdapter extends RecyclerView.Adapter<ImagesRecyclerAdapter.ViewHolder> {
    
    private Activity activity;
    private int layout, size;
    private List<ImageBrowser.Item> filesList;
    
    private Listener listener;
    
    ImagesRecyclerAdapter (Activity activity, int layout, List<ImageBrowser.Item> filesList, final int size) {
      
      this.activity = activity;
      this.layout = layout;
      this.filesList = filesList;
      this.size = size;
      
    }
    
    interface Listener {
      
      void onView (ViewHolder holder);
      void onItemClick (ViewHolder holder, ImageBrowser.Item item);
      boolean onItemLongClick (ViewHolder holder, ImageBrowser.Item item);
      
    }
    
    ImagesRecyclerAdapter setListener (Listener listener) {
      
      this.listener = listener;
      return this;
      
    }
    
    @NonNull
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
      
      LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
      View view = inflater.inflate (layout, parent, false);
      
      return new ViewHolder (view);
      
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
      
      ImageView imageView;
      View alphaView;
      FrameLayout layout;
      TextView textView, eTextView;
      ProgressBar progressBar;
      
      public ViewHolder (View view) {
        
        super (view);
        
        layout = view.findViewById (R.id.grid_item);
        alphaView = view.findViewById (R.id.alpha_view);
        imageView = view.findViewById (R.id.grid_item_image);
        textView = view.findViewById (R.id.grid_item_label);
        eTextView = view.findViewById (R.id.grid_item_text);
        progressBar = view.findViewById (R.id.progress_bar);
        
      }
      
    }
    
    @Override
    public void onBindViewHolder (final @NonNull ViewHolder holder, int pos) {
      
      //holder.imageView.setImageDrawable (null);
      
      //if (getItemViewType (position))
      //	holder.imageView.setVisibility (View.GONE);
      //else
      
      final ImageBrowser.Item item = filesList.get (holder.getAdapterPosition ());
      
      if (Int.size (item.outErrors) > 0) {
        
        holder.eTextView.setVisibility (View.VISIBLE);
        
        holder.eTextView.setText (Arrays.implode (item.outErrors));
        holder.eTextView.setWidth (size);
        
      } else if (Int.size (item.errors) > 0) {
        
        holder.eTextView.setVisibility (View.VISIBLE);
        
        holder.eTextView.setText (Arrays.implode (item.errors));
        holder.eTextView.setWidth (size);
        
      } else {
        
        if (item.image != null) {
          
          holder.imageView.setVisibility (View.VISIBLE);
          holder.imageView.setImageBitmap (item.image);
          
        }
        
        if (item.title != null) {
          
          holder.textView.setVisibility (View.VISIBLE);
          
          holder.textView.setText (item.title);
          holder.textView.setWidth (size);
          
        }
        
      }
      
      holder.imageView.setListener (new ImageView.OnImageListener () {
        
        @Override
        public void onView () {
          if (listener != null) listener.onView (holder);
        }
        
      });
      
      holder.layout.setOnClickListener (new View.OnClickListener () {
        
        @Override
        public void onClick (View view) {
          if (listener != null) listener.onItemClick (holder, item);
        }
        
      });
      
      holder.layout.setOnLongClickListener (new View.OnLongClickListener () {
        
        @Override
        public boolean onLongClick (View view) {
          return (listener == null || listener.onItemLongClick (holder, item));
        }
        
      });
      
    }
    
    @Override
    public int getItemCount () {
      return Int.size (filesList);
    }
    
    @Override
    public int getItemViewType (int position) {
      return position;
    }
    
  }