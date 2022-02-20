  package ru.ointeractive.imagebrowser;
  /*
   Created by Acuna on 13.02.2019
  */
  
  import android.view.LayoutInflater;
  import android.view.View;
  import android.view.ViewGroup;
  import android.widget.BaseAdapter;
  import android.widget.FrameLayout;
  import android.widget.ProgressBar;
  import android.widget.TextView;
  
  import upl.util.List;

  import ru.ointeractive.androdesign.widget.ImageView;
  import ru.ointeractive.jabadaba.Arrays;
  import ru.ointeractive.jabadaba.Int;
  import ru.ointeractive.jabadaba.Log;
  
  public class ImagesGridAdapter extends BaseAdapter {
    
    private int layout, size;
    private List<ImageBrowser.Item> items;
    
    ImagesGridAdapter (int layout, List<ImageBrowser.Item> items, int size) {
      
      this.layout = layout;
      this.items = items;
      this.size = size;
      
    }
    
    private Listener listener;
    
    interface Listener {
      
      void onView (ViewHolder holder);
      void onItemClick (ViewHolder holder, ImageBrowser.Item item);
      boolean onItemLongClick (ViewHolder holder, ImageBrowser.Item item);
      
    }
    
    ImagesGridAdapter setListener (Listener listener) {
      
      this.listener = listener;
      return this;
      
    }
    
    @Override
    public int getCount () {
      return Int.size (items);
    }
    
    @Override
    public long getItemId (int position) {
      return position;
    }
    
    @Override
    public Object getItem (int position) {
      return null;
    }
    
    public static class ViewHolder {
      
      FrameLayout gridLayout;
      View alphaView;
      ImageView imageView;
      TextView labelView;
      TextView textView;
      ProgressBar progressBar;
      
    }
    
    @Override
    public View getView (final int pos, View view, ViewGroup parent) {
      
      final ViewHolder holder;
      
      if (view == null) {
        
        holder = new ViewHolder ();
        
        view = LayoutInflater.from (parent.getContext ()).inflate (layout, null);
        
        //RelativeLayout layout = view.findViewById (R.id.progress_bar_layout);
        //layout.setLayoutParams (new RelativeLayout.LayoutParams (size, size));
        
        holder.gridLayout = view.findViewById (R.id.grid_item);
        holder.alphaView = view.findViewById (R.id.alpha_view);
        holder.imageView = view.findViewById (R.id.grid_item_image);
        holder.labelView = view.findViewById (R.id.grid_item_label);
        holder.textView = view.findViewById (R.id.grid_item_text);
        holder.progressBar = view.findViewById (R.id.progress_bar);
        
        view.setTag (holder);
        
      } else holder = (ViewHolder) view.getTag ();
      
      final ImageBrowser.Item item = items.get (pos);
      
      if (Int.size (item.outErrors) > 0) {
        
        holder.textView.setVisibility (View.VISIBLE);
        
        holder.textView.setText (Arrays.implode (item.outErrors));
        holder.textView.setWidth (size);
        
      } else if (Int.size (item.errors) > 0) {
        
        holder.textView.setVisibility (View.VISIBLE);
        
        holder.textView.setText (Arrays.implode (item.errors));
        holder.textView.setWidth (size);
        
      } else {
        
        if (item.image != null) {
          
          holder.imageView.setVisibility (View.VISIBLE);
          holder.imageView.setImageBitmap (item.image);
          
        }
        
        if (item.title != null) {
          
          holder.labelView.setVisibility (View.VISIBLE);
          
          holder.labelView.setText (item.title);
          holder.labelView.setWidth (size);
          
        } else holder.labelView.setVisibility (View.GONE);
        
      }
      
      holder.imageView.setListener (new ImageView.OnImageListener () {
        
        @Override
        public void onView () {
          if (listener != null) listener.onView (holder);
        }
        
      });
      
      holder.gridLayout.setOnClickListener (new View.OnClickListener () {
        
        @Override
        public void onClick (View view) {
          if (listener != null) listener.onItemClick (holder, item);
        }
        
      });
      
      holder.gridLayout.setOnLongClickListener (new View.OnLongClickListener () {
        
        @Override
        public boolean onLongClick (View view) {
          return (listener == null || listener.onItemLongClick (holder, item));
        }
        
      });
      
      return view;
      
    }
    
  }