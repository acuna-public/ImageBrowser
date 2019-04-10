	package pro.acuna.imagebrowser;
	/*
	 Created by Acuna on 13.02.2019
	*/
	
	import android.app.Activity;
	import android.graphics.drawable.Drawable;
	import android.os.AsyncTask;
	import android.os.Build;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.view.ViewTreeObserver;
	import android.widget.BaseAdapter;
	import android.widget.FrameLayout;
	import android.widget.ImageView;
	import android.widget.ProgressBar;
	import android.widget.RelativeLayout;
	import android.widget.TextView;
	
	import java.util.ArrayList;
	import java.util.List;
  
  import pro.acuna.filedialog.Provider;
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.StorageException;
	
	public class ImagesGridAdapter extends BaseAdapter {
		
		private int layout, size = 0;
		private Activity activity;
		private List<Provider> filesList;
		
		ImagesGridAdapter (Activity activity, int layout, List<Provider> filesList, int size) {
			
			this.activity = activity;
			this.layout = layout;
			this.filesList = filesList;
			this.size = size;
			
		}
		
		private Listener listener;
		
		interface Listener {
			
			void onView (View view);
			void onItemClick (View view, int position, Provider item);
			boolean onItemLongClick (View view, int position, Provider item);
			
		}
		
		ImagesGridAdapter setListener (Listener listener) {
			
			this.listener = listener;
			return this;
			
		}
		
		@Override
		public int getCount () {
			return Int.size (filesList);
		}
		
		@Override
		public long getItemId (int position) {
			return position;
		}
		
		@Override
		public Object getItem (int position) {
			return null;
		}
		
		@Override
		public View getView (int position, View view, ViewGroup parent) {
			
			if (view == null) {
				
				view = LayoutInflater.from (activity).inflate (layout, null);
				
				RelativeLayout layout = view.findViewById (R.id.progress_bar_layout);
				layout.setLayoutParams (new RelativeLayout.LayoutParams (size, size));
				
			}
			
			new ShowItems (view, position).execute ();
			
			return view;
			
		}
		
		private class ShowItems extends AsyncTask<Void, Void, Void> {
			
			private View view;
			private int pos;
			private Drawable image;
			private String title;
			private Provider item;
			private List<Exception> errors = new ArrayList<> (), outErrors = new ArrayList<> ();
			
			private ShowItems (View view, int pos) {
				
				this.view = view;
				this.pos = pos;
				
				item = filesList.get (pos);
				
			}
			
			@Override
			public Void doInBackground (Void... params) {
				
				try {
					
					if (item.isDir ()) {
						
						image = item.folderCover ();
						title = item.folderTitle ();
						
					} else image = item.getImage ();
					
				} catch (StorageException e) {
					errors.add (e);
				} catch (OutOfMemoryException e) {
					outErrors.add (e);
				}
				
				return null;
				
			}
			
			@Override
			public void onPostExecute (Void params) {
				
				FrameLayout grid = view.findViewById (R.id.grid_item);
				final ImageView imageView = view.findViewById (R.id.grid_item_image);
				TextView textView = view.findViewById (R.id.grid_item_label);
				TextView eTextView = view.findViewById (R.id.grid_item_text);
				ProgressBar progressBar = view.findViewById (R.id.progress_bar);
				
				progressBar.setVisibility (View.GONE);
				
				if (Int.size (outErrors) > 0) {
					
					eTextView.setVisibility (View.VISIBLE);
					
					eTextView.setText (Arrays.implode (outErrors));
					eTextView.setWidth (size);
					
				} else if (Int.size (errors) > 0) {
					
					eTextView.setVisibility (View.VISIBLE);
					
					eTextView.setText (Arrays.implode (errors));
					eTextView.setWidth (size);
					
				} else {
					
					if (image != null) {
						
						imageView.setVisibility (View.VISIBLE);
						imageView.setImageDrawable (image);
						
					}
					
					if (title != null) {
						
						textView.setVisibility (View.VISIBLE);
						
						textView.setText (title);
						textView.setWidth (size);
						
					}
					
				}
				
				imageView.getViewTreeObserver ().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener () {
					
					@Override
					public void onGlobalLayout () {
						
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
							imageView.getViewTreeObserver ().removeGlobalOnLayoutListener (this);
						else
							imageView.getViewTreeObserver ().removeOnGlobalLayoutListener (this);
						
						if (listener != null) listener.onView (view);
						
					}
					
				});
				
				grid.setOnClickListener (new View.OnClickListener () {
					
					@Override
					public void onClick (View view) {
						
						if (listener != null)
							listener.onItemClick (view, pos, item);
						
					}
					
				});
				
			}
			
		}
		
	}