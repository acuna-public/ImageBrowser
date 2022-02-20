  package ru.ointeractive.imagebrowser;
  /*
   Created by Acuna on 11.05.2018
  */
  
  import android.graphics.Bitmap;
  
  import java.io.IOException;
  
  import ru.ointeractive.andromeda.graphic.Graphic;
  import ru.ointeractive.filedialog.Provider;
  import ru.ointeractive.jabadaba.Arrays;
  import ru.ointeractive.jabadaba.Files;
  import upl.core.Log;
  import upl.core.exceptions.HttpRequestException;
  import upl.core.exceptions.OutOfMemoryException;
  import ru.ointeractive.jstorage.Item;
  import ru.ointeractive.jstorage.StorageException;
  
  public class Images extends Provider {
    
    protected ImageBrowser browser;
    
    public Images (ImageBrowser browser, Item item) {
      
      this.browser = browser;
      this.item = item;
      
    }
    
    @Override
    public Provider newInstance (Item item) {
      return new Images (browser, item);
    }
    
    @Override
    public Bitmap getImage () throws IOException, HttpRequestException, StorageException, OutOfMemoryException {
	    return Graphic.toBitmap (Arrays.toByteArray (item.getThumbStream ()), 300, 300);
    }
    
    @Override
    public String folderTitle () {
      return Files.getName (item.getShortFile (), true);
    }
    
  }