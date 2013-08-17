package com.lutka.notemap;

import java.io.IOException;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lutka.notemap.AddressFinder.OnAddressFoundListener;

public class Note implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	String noteTitle;
	String noteDestription;
	LatLng noteLocation;
	String noteSubTitle;
	transient Marker noteMarker = null;
	//int cameraZoom;                  

	// holds pin id in the array
	int pinId = 0;
	
	public static final Integer [] pinIds = { 
		R.drawable.pin_questionmark,
		R.drawable.pin_car,
		R.drawable.pin_gift,
		R.drawable.pin_key,
		R.drawable.pin_music,
		R.drawable.pin_shopping,
		R.drawable.pin_anchor,
		R.drawable.pin_basket,
		R.drawable.pin_info,
		R.drawable.pin_memo,
		R.drawable.pin_paper,
		R.drawable.pin_tools,
		R.drawable.pin_train,
		
		R.drawable.pin_android,
		R.drawable.pin_bike,
		R.drawable.pin_flower,
		R.drawable.pin_food,
		R.drawable.pin_fruit,
		R.drawable.pin_heart,
		R.drawable.pin_house,
		R.drawable.pin_pet,
		};
	
	public static Drawable getPinDrawable(Context contex, int pinName) throws IOException
	{
		return contex.getResources().getDrawable(pinName);
	}
	
	public Drawable getPinDrawable(Context context) throws IOException
	{
		return getPinDrawable(context, pinIds[pinId]);
	}
	
	/*public String getMarkerId()
	{
		return ;
		
	}*/
		
	public Note(String noteTitle, String noteSubTitle, String noteDescription,LatLng noteLocation)
	{
		this.noteTitle = noteTitle;
		this.noteSubTitle = noteSubTitle;
		this.noteDestription =  noteDescription;
		this.noteLocation = noteLocation;		
	}
	
	public Note (JSONObject jsonObject) throws JSONException
	{
		importNote(jsonObject);
	}
	
	public Marker addToMap(GoogleMap map)
	{
		/*String description = this.getNoteDestription();
		String snipper;
		if(description.length() <= 10) snipper = description;
		else snipper =  description.substring(0, 10);*/
			
		// creat a mapMarker to use it later in the order to connect a note with its marker on a map 
		noteMarker = map.addMarker(new MarkerOptions()
		.position(this.getNoteLocation()).title(this.getNoteTitle())
		.snippet(this.getNoteDestription()).draggable(true));
		
		return noteMarker;
	}
	
	public void removeFromMap ()
	{
		if (noteMarker != null)	noteMarker.remove();
	}
	
	public void updateMarker()
	{
		if(noteMarker != null)
		{
			// to make a snipper have a particular size/length 
			//String description = noteDestription;
			String subTitle = noteSubTitle;
			String content = noteDestription;
			String snipper;
			if(subTitle.length() == 0) 
				{
					if(content.length() == 0)snipper = "";
					else if (content.length() <= getNoteTitle().length()) snipper = content;
					else snipper =  content.substring(0, getNoteTitle().length());
				
				}
			else if(subTitle.length() <= noteTitle.length()) snipper = noteSubTitle;
			else snipper =  subTitle;//.substring(0, noteTitle.length()); //
			
			noteMarker.setTitle(getNoteTitle());
			noteMarker.setSnippet(snipper);	

			try
			{
				noteMarker.setIcon(getBitmapDescriptor());
				
				
				if(noteMarker.isInfoWindowShown())
				{
					noteMarker.hideInfoWindow();
					noteMarker.showInfoWindow();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}			
		}		
	}
	
	public BitmapDescriptor getBitmapDescriptor()
	{
		return BitmapDescriptorFactory.fromResource(pinIds[pinId]);
	}	
	
	public LatLng getNoteLocation()
	{
		return noteLocation;
	}
	
	public String getNoteTitle()
	{
		if(TextUtils.isEmpty(noteTitle))
		{
			return "Unknown address";
		}
		return noteTitle;
	}
	
	
	public String getNoteSubTitle() 
	{
		return noteSubTitle;
	}
	
	public String getNoteDestription()
	{
		return noteDestription;
	}
	
	
	/**
	 * Set pin resource to note
	 * @param pin Pin resource
	 */
	public void setPin(int pin) 
	{
		for (int i=0; i < pinIds.length; i++)
		{
			if (pinIds[i] == pin)
			{
				this.pinId = i;
				updateMarker();
				return;
			}
		}
		
	}
	
	public void setNoteTitle(String noteTitle)
	{
		this.noteTitle = noteTitle;
		updateMarker();
	}
	
	public void setNoteSubTitle(String noteSubTitle) 
	{
		this.noteSubTitle = noteSubTitle;
		updateMarker();
	}
	
	public void setNoteDestription(String noteDestription)
	{
		this.noteDestription = noteDestription;
		updateMarker();
	}
	public void setNoteLocation(LatLng noteLocation)
	{
		this.noteLocation = noteLocation;
	}
	

	public void findNoteAddress( Context context, final int zoom)
	{		
		Log.d(toString(), "Wyszukiwanie lokalizacji");
			new AddressFinder(context).
				setOnAddressFoundListener(new OnAddressFoundListener() 
			{				
				@Override
				public void onAddressFound(Address address) 
				{
					if (address == null)
						return;
					
					Log.d(toString(), "Address Found "+address.toString());
					String countryName = address.getCountryName();
					String subAdminArea = address.getSubAdminArea();
					String addressLine_0 = address.getAddressLine(0);
					String addressLine_1 = address.getAddressLine(1);
					
			/*		if(countryName == null) countryName = "";
					if(subAdminArea == null) subAdminArea = "";
					if(addressLine_0 == null) addressLine_0 = "";
					if(addressLine_1 == null) addressLine_1 = "";*/
					
					
					if(zoom >= 12)
					{
						if(subAdminArea != null && countryName!=null) noteTitle = String.format("%s, %s", subAdminArea, countryName); //noteTitle = subAdminArea+", "+countryName;
						else noteTitle = String.format("%s, %s", addressLine_0, addressLine_1);
						//noteDestription =  String.valueOf(zoom);					
					}
					
					else if (zoom < 12 &&  zoom >= 4) 					
					{
						if(subAdminArea != null ) noteTitle = String.format("%s, %s",addressLine_0 , subAdminArea);
						else noteTitle = String.format("%s, %s",addressLine_0 , addressLine_1);
						//noteDestription =  String.valueOf(zoom);					
					}
					
					else
					{
						noteTitle = String.format("%s, %s",addressLine_0 , addressLine_1);
						//noteDestription =  String.valueOf(zoom);
					}
					
					/*
					// --> for setting the right parameters in title
					noteTitle = String.valueOf(zoom);
					
					noteDestription = "1)"+address.getAddressLine(0)+"\n 2) "+address.getAddressLine(1)+"\n 3) "
							+ address.getAddressLine(2)+"\n Country name: "+address.getCountryName().toUpperCase()
							+"\n Feature name: "+address.getFeatureName().toUpperCase()+"\n Country code: "
							+ address.getCountryCode().toUpperCase()+"\n SubAdminArea: "+address.getSubAdminArea().toUpperCase();				
					// <--	ends here
					 * */						
						
					updateMarker();	
					
				}
			})
			.execute(noteLocation);		
			
	}
	
	///////// 
	//JSONObject similar to bundle, but it save data as text
	//used to send data from server to the app
	public JSONObject exportNote() throws JSONException
	{		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("title", noteTitle);
		jsonObject.put("description", noteDestription);
		jsonObject.put("subTitle", noteSubTitle);
		jsonObject.put("pin", pinId);
		jsonObject.put("latitude", noteLocation.latitude);
		jsonObject.put("longitude", noteLocation.longitude);
		return jsonObject;
	}
	
	public void importNote(JSONObject jsonObject) throws JSONException
	{
		noteTitle = jsonObject.getString("title");
		noteDestription = jsonObject.getString("description");
		noteSubTitle = jsonObject.getString("subTitle");
		
		if(jsonObject.has("pin")) 
		{
			pinId = jsonObject.getInt("pin");
		}
		
		noteLocation = new LatLng (jsonObject.getDouble("latitude")
				,jsonObject.getDouble("longitude"));	
	}
	
	public boolean isEmpty()
	{
//		return noteDestription.isEmpty() && noteSubTitle.isEmpty();
		return TextUtils.isEmpty(noteDestription) && TextUtils.isEmpty(noteSubTitle);
	}
	
	public boolean isAddressEmpty()
	{
		return TextUtils.isEmpty(noteTitle);		
	}
	
	void showPinDialog(Context context, final OnItemClickListener itemClickListener)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View dialogView = inflater.inflate(R.layout.pin_selection_dialog, null);
		GridView gridIcons = (GridView) dialogView.findViewById(R.id.gridIcons);
		gridIcons.setAdapter(new PinAdapter(context, Note.pinIds));
		
		AlertDialog.Builder builder = new Builder(context);
		builder.setView(dialogView).setTitle(R.string.change_note_pin);		
		builder.setView(dialogView).setNegativeButton(android.R.string.cancel, null);
		final Dialog dialog = builder.create();
		
		gridIcons.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) 
			{
				Integer pinName = (Integer) adapterView.getItemAtPosition(position);
				setPin(pinName);
				itemClickListener.onItemClick(adapterView, view, position, id);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Note)
			return noteLocation.equals(((Note) o).noteLocation);
		else return super.equals(o);
	}
	
	@Override
	public int hashCode()
	{
		return noteLocation.hashCode();
	}

}
