	package pro.acuna.imagebrowser;
	/*
	 Created by Acuna on 10.05.2018
	*/
	
	import android.app.Activity;
	import android.os.AsyncTask;
	import android.view.View;
	
	import java.util.ArrayList;
	import java.util.List;
	import java.util.concurrent.ExecutionException;
	
	import pro.acuna.androdesign.widget.GridLayoutManager;
	import pro.acuna.androdesign.widget.GridView;
	import pro.acuna.androdesign.widget.NestedScrollView;
	import pro.acuna.androdesign.widget.RecyclerView;
	import pro.acuna.androdesign.widget.ScrollView;
  import pro.acuna.filedialog.Provider;
	import pro.acuna.jabadaba.Int;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.StorageException;
	
	public class ImageBrowser {
		
		private Activity activity;
		private int size = 150, page = 0;
		private Provider provider;
		private List<Provider> files = new ArrayList<> ();
		
		private ImagesAdapter recyclerAdapter;
		private ImagesGridAdapter gridAdapter;
		
		private ScrollView scrollView;
		private NestedScrollView nestedScrollView;
		
		public ImageBrowser (Activity activity) {
			this.activity = activity;
		}
		
		public interface Listener {
			
			void onItemClick (View view, int position, Provider item);
			boolean onItemLongClick (View view, int position, Provider item);
			void onScrollEnd (Provider provider);
			void onError (Exception e);
			void onOutOfMemoryError (Exception e);
			
		}
		
		private Listener listener;
		
		public ImageBrowser setListener (Listener listener) {
			
			this.listener = listener;
			return this;
			
		}
		
		public GridView gridView;
		public RecyclerView recyclerView;
		
		public ImageBrowser setView (View view) {
			
			if (view instanceof GridView)
				this.gridView = (GridView) view;
			else if (view instanceof RecyclerView)
				this.recyclerView = (RecyclerView) view;
			
			return this;
			
		}
		
		public ImageBrowser setScrollView (View view) {
			
			if (view instanceof ScrollView)
				this.scrollView = (ScrollView) view;
			else if (view instanceof NestedScrollView)
				this.nestedScrollView = (NestedScrollView) view;
			
			return this;
			
		}
		
		public ImageBrowser setImageWidth (int size) {
			
			this.size = size;
			return this;
			
		}
		
		public int getImageSize () {
			return size;
		}
		
		public ImageBrowser start () {
			
			if (gridView != null) {
				
				gridView.setVisibility (View.VISIBLE);
				gridView.setColumnWidth (size);
				
				gridView.setAdapter (gridAdapter);
				
			} else if (recyclerView != null) {
				
				//recyclerView = activity.findViewById (R.id.grid_view);
				
				recyclerView.setLayoutManager (new GridLayoutManager (activity).setWidth (getImageSize ()));
				
				recyclerView.setVisibility (View.VISIBLE);
				
				recyclerView.setAdapter (recyclerAdapter);
				
			}
			
			if (scrollView != null) {
				
				scrollView.setListener (new ScrollView.OnScrollListener () {
					
					@Override
					public void onScrollBottomEnd (View view) {
						if (listener != null) listener.onScrollEnd (provider);
					}
					
				});
				
			} else if (nestedScrollView != null) {
				
				nestedScrollView.setListener (new NestedScrollView.OnScrollListener () {
					
					@Override
					public void onScrollBottomEnd (View view) {
						if (listener != null) listener.onScrollEnd (provider);
					}
					
				});
				
			} else if (recyclerView != null) {
				
				recyclerView.setListener (new RecyclerView.OnScrollListener () {
					
					@Override
					public void onScrollBottomEnd (View recyclerView, int scrollState) {
						if (listener != null) listener.onScrollEnd (provider);
					}
					
				});
				
			} else {
				
				gridView.setListener (new GridView.OnScrollListener () {
					
					@Override
					public void onScrollBottomEnd (View view, int scrollState) {
						if (listener != null) listener.onScrollEnd (provider);
					}
					
				});
				
			}
			
			return this;
			
		}
		
		public ImageBrowser clear () {
			
			files.clear ();
			return this;
			
		}
		
		private int imageLayout = 0;
		
		public ImageBrowser setImageLayout (int layout) {
			
			imageLayout = layout;
			return this;
			
		}
		
		public ImageBrowser setProvider (Provider provider) {
			
			this.provider = provider;
			
			try {
				
				++page;
				
				files.addAll (new ShowItems ().execute ().get ());
				
				if (gridView != null) {
					
					if (gridAdapter == null) {
						
						if (imageLayout == 0) imageLayout = R.layout.item_image;
						gridAdapter = new ImagesGridAdapter (activity, imageLayout, files, getImageSize ());
						
						gridAdapter.setListener (new ImagesGridAdapter.Listener () {
							
							@Override
							public void onView (View view) {
								
								//ViewGroup.LayoutParams params = holder.imageView.getLayoutParams ();
								
								//int width = holder.imageView.getMeasuredWidth ();
								
								//params.width = width;
								//params.height = width;
								
							}
							
							@Override
							public void onItemClick (View view, int position, Provider item) {
								
								if (listener != null)
									listener.onItemClick (view, position, files.get (position));
								
							}
							
							@Override
							public boolean onItemLongClick (View view, int position, Provider item) {
								return (listener == null || listener.onItemLongClick (view, position, files.get (position)));
							}
							
						});
						
					} else {
						
						gridAdapter.notifyDataSetChanged ();
						
					}
					
				} else if (recyclerView != null) {
					
					if (recyclerAdapter == null) {
						
						if (imageLayout == 0) imageLayout = R.layout.item_image;
						recyclerAdapter = new ImagesAdapter (activity, imageLayout, files, size);
						
						recyclerAdapter.setListener (new ImagesAdapter.Listener () {
							
							@Override
							public void onView (ImagesAdapter.ViewHolder holder) {
								
								//ViewGroup.LayoutParams params = holder.imageView.getLayoutParams ();
								
								//int width = holder.imageView.getMeasuredWidth ();
								
								//params.width = width;
								//params.height = width;
								
							}
							
							@Override
							public void onItemClick (View view, int position, Provider item) {
								
								if (listener != null)
									listener.onItemClick (view, position, files.get (position));
								
							}
							
							@Override
							public boolean onItemLongClick (View view, int position, Provider item) {
								return (listener == null || listener.onItemLongClick (view, position, files.get (position)));
							}
							
						});
						
					} else {
						
						int newPage = (page * provider.perPage ());
						
						recyclerAdapter.notifyItemInserted (newPage);
						recyclerAdapter.notifyItemRangeChanged (newPage, (newPage * 2));
						
					}
					
				}
				
			} catch (InterruptedException | ExecutionException e) {
				listener.onError (e);
			}
			
			return this;
			
		}
		
		public ImagesAdapter getRecyclerAdapter () {
			return recyclerAdapter;
		}
		
		public boolean success () {
			return (Int.size (files) > 0);
		}
		
		private class ShowItems extends AsyncTask<Void, Void, List<Provider>> {
			
			private List<Exception> errors = new ArrayList<> (), outErrors = new ArrayList<> ();
			
			@Override
			public void onPreExecute () {
			
			}
			
			@Override
			public List<Provider> doInBackground (Void... params) {
				
				List<Provider> files = new ArrayList<> ();
				
				try {
					files = provider.list ();
				} catch (StorageException e) {
					errors.add (e);
				} catch (OutOfMemoryException e) {
					outErrors.add (e);
				}
				
				return files;
				
			}
			
			@Override
			public void onPostExecute (final List<Provider> files) {
				
				if (Int.size (errors) > 0)
					for (Exception e : errors)
						listener.onError (e);
				
				if (Int.size (outErrors) > 0)
					for (Exception e : outErrors)
						listener.onOutOfMemoryError (e);
				
			}
			
		}
		
	}