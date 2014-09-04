package com.example.babydays;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardView;
import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.Menu;
import android.view.MenuItem;

public class TryCardView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_try_card_view);
		
		//Create a Card
	      Card card = new Card(this);

	      //Set the card inner text
	      card.setTitle("My Title");
	    //Create a CardHeader
	      CardHeader header = new CardHeader(this);
	      header.setTitle("HEAD TITLE");
	      //Add Header to card
	      card.addCardHeader(header);
	      CardThumbnail thumbnail = new CardThumbnail(this);
	      thumbnail.setDrawableResource(R.drawable.green_star);
	      card.addCardThumbnail(thumbnail);
	      //Set card in the cardView
	      CardView cardView = (CardView) findViewById(R.id.carddemo_thumb_url);
	      cardView.setCard(card);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.try_card_view, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
