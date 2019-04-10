	package pro.acuna.imagebrowser;
	/*
	 Created by Acuna on 11.05.2018
	*/
	
	import android.graphics.drawable.Drawable;
	
	import org.json.JSONException;
	
	import java.io.IOException;
	import java.util.ArrayList;
	import java.util.List;
  
  import pro.acuna.andromeda.Graphic;
  import pro.acuna.filedialog.Provider;
  import pro.acuna.jabadaba.Files;
	import pro.acuna.jabadaba.exceptions.HttpRequestException;
	import pro.acuna.jabadaba.exceptions.OutOfMemoryException;
	import pro.acuna.storage.Item;
	import pro.acuna.storage.StorageException;
	
	public class Images extends Provider {
		
		private ImageBrowser browser;
		
		public Images (ImageBrowser browser, Item item) {
			
			super (item);
			
			this.browser = browser;
			
		}
		
		@Override
		public Provider toProvider (Item item) {
			return new Images (null, item);
		}
		
		@Override
		public List<Provider> list () throws StorageException, OutOfMemoryException {
			
			List<Provider> output = new ArrayList<> ();
			
			List<Item> files = item.list ();
			
			for (Item file : files)
				output.add (new Images (browser, file));
			
			return sort (output);
			
		}
		
		@Override
		public Drawable getImage () throws StorageException, OutOfMemoryException {
			
			try {
				return Graphic.toDrawable (Graphic.toBitmap (item.getDirectLink (), item.storage.settings.getString ("useragent"), browser.getImageSize (), browser.getImageSize ()));
			} catch (IOException | HttpRequestException | JSONException e) {
				throw new StorageException (e);
			}
			
		}
		
		@Override
		public Drawable folderCover () throws StorageException, OutOfMemoryException {
			
			List<Item> files = item.list ();
			
			for (Item file : files) {
				
				if (!file.isDir () && file.isImage ())
					return new Images (browser, file).getImage ();
				
			}
			
			return null;
			
		}
		
		@Override
		public boolean isDir () {
			return item.isDir ();
		}
		
		@Override
		public String folderTitle () {
			return Files.getName (item.getFile ());
		}
		
	}