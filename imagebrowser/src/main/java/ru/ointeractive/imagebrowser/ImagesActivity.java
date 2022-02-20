  package ru.ointeractive.imagebrowser;
  /*
   Created by Acuna on 10.05.2018
  */
  
  import android.os.Bundle;
  import android.support.v7.app.AppCompatActivity;
  import android.view.Menu;
  
  import com.github.clans.fab.FloatingActionButton;
  
  import ru.ointeractive.androdesign.widget.Toolbar;
  import ru.ointeractive.wrapper.Andromeda;

  public class ImagesActivity extends AppCompatActivity {
    
    Toolbar toolbar;
    
    Andromeda andro;
    
    FloatingActionButton fab;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
      
      super.onCreate (savedInstanceState);
      
      andro = new Andromeda (this, "");
      
      andro.init (this);
      
      Bundle bundle = getIntent ().getExtras ();
      
      setContentView (R.layout.activity_images);
      
      toolbar = findViewById (R.id.toolbar);
      
      toolbar.setHomeButton (true);
      
      fab = findViewById (R.id.fab);
      
      new ImagesFragment ().newFragment (this, bundle, R.layout.content_images_grid, false);
      
    }
    
    //private MenuItem doneButton;
    
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
      
      getMenuInflater ().inflate (R.menu.ip_main, menu);
      
      //doneButton = menu.findItem (R.id.ip_action_done);
      //doneButton.setVisible (false);
      
      return true;
      
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
      
      //doneButton.setVisible (Int.size (selItems) > 0);
      
      return super.onPrepareOptionsMenu (menu);
      
    }
    
  }