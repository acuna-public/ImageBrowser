	package pro.acuna.imagebrowser;
	/*
	 Created by Acuna on 19.12.2018
	*/
	
	import android.app.Activity;
	import android.graphics.drawable.Drawable;
	import android.os.AsyncTask;
	import android.os.Build;
	import android.support.annotation.NonNull;
	import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.view.ViewTreeObserver;
	import android.widget.FrameLayout;
	import android.widget.ImageView;
	import android.widget.ProgressBar;
	import android.widget.TextView;
	
	import java.util.ArrayList;
	import java.util.List;
	
	import pro.acuna.androdesign.widget.RecyclerView;
  import pro.acuna.filedialog.Provider;
	import pro.acuna.jabadaba.Arrays;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.StorageException;
	
	public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
		
		private int layout, size;
		private Activity activity;
		private List<Provider> filesList;
		
		ImagesAdapter (Activity activity, int layout, List<Provider> filesList, int size) {
			
			this.activity = activity;
			this.layout = layout;
			this.filesList = filesList;
			this.size = size;
			
		}
		
		private Listener listener;
		
		interface Listener {
			
			void onView (ViewHolder holder);
			void onItemClick (View view, int position, Provider item);
			boolean onItemLongClick (View view, int position, Provider item);
			
		}
		
		ImagesAdapter setListener (Listener listener) {
			
			this.listener = listener;
			return this;
			
		}
		
		@NonNull
		public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
			
			LayoutInflater inflater = LayoutInflater.from (parent.getContext ());
			View view = inflater.inflate (layout, parent, false);
			
			return new ViewHolder (view);
			
		}
		
		@Override
		public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
			
			holder.imageView.setImageDrawable (null);
			
			//if (getItemViewType (position))
			//	holder.imageView.setVisibility (View.GONE);
			//else
				new ShowItems (holder, position).execute ();
			
		}
		
		@Override
		public int getItemCount () {
			return Int.size (filesList);
		}
		
		@Override
		public int getItemViewType (int position) {
			return position;
		}
		
		static class ViewHolder extends RecyclerView.ViewHolder {
			
			ImageView imageView;
			FrameLayout layout;
			TextView textView, eTextView;
			ProgressBar progressBar;
			
			ViewHolder (View view) {
				
				super (view);
				
				layout = view.findViewById (R.id.grid_item);
				imageView = view.findViewById (R.id.grid_item_image);
				textView = view.findViewById (R.id.grid_item_label);
				eTextView = view.findViewById (R.id.grid_item_text);
				progressBar = view.findViewById (R.id.progress_bar);
				
			}
			
		}
		
		private class ShowItems extends AsyncTask<Void, Void, Void> {
			
			private int pos;
			private ViewHolder holder;
			private Drawable image;
			private String title;
			private Provider item;
			private List<Exception> errors = new ArrayList<> (), outErrors = new ArrayList<> ();
			
			private ShowItems (ViewHolder holder, int pos) {
				
				this.holder = holder;
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
				
				holder.progressBar.setVisibility (View.GONE);
				
				if (Int.size (outErrors) > 0) {
					
					holder.eTextView.setVisibility (View.VISIBLE);
					
					holder.eTextView.setText (Arrays.implode (outErrors));
					holder.eTextView.setWidth (size);
					
				} else if (Int.size (errors) > 0) {
					
					holder.eTextView.setVisibility (View.VISIBLE);
					
					holder.eTextView.setText (Arrays.implode (errors));
					holder.eTextView.setWidth (size);
					
				} else {
					
					if (image != null) {
						
						holder.imageView.setVisibility (View.VISIBLE);
						holder.imageView.setImageDrawable (image);
						
					}
					
					if (title != null) {
						
						holder.textView.setVisibility (View.VISIBLE);
						
						holder.textView.setText (title);
						holder.textView.setWidth (size);
						
					}
					
				}
				
				holder.imageView.getViewTreeObserver ().addOnGlobalLayoutListener (new ViewTreeObserver.OnGlobalLayoutListener  () {
					
					@Override
					public void onGlobalLayout () {
						
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
							holder.imageView.getViewTreeObserver ().removeGlobalOnLayoutListener (this);
						else
							holder.imageView.getViewTreeObserver ().removeOnGlobalLayoutListener (this);
						
						if (listener != null) listener.onView (holder);
						
					}
					
				});
				
				holder.layout.setOnClickListener (new View.OnClickListener () {
					
					@Override
					public void onClick (View view) {
						
						if (listener != null)
							listener.onItemClick (view, pos, item);
						
					}
					
				});
				
			}
			
		}
		
	}