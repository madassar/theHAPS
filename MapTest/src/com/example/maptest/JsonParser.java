package com.example.maptest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

public class JsonParser {
    
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader = null;
	private JsonReader reader = null;
	private ArrayList<Double> Latitude;
	private ArrayList<Double> Longitude;
	private ArrayList<String> localname;
	private ArrayList<Integer> priority;
	private ArrayList<Integer> clicks;
	private ArrayList<Integer> color;
	private ArrayList<String> name;
	private ArrayList<String> Short;
	private ArrayList<String> start;
	private ArrayList<String> stop;
	private ArrayList<String> first_name;
	private ArrayList<String> profile;
	
	JsonParser(InputStream inputStream)
	{
		inputStreamReader = new InputStreamReader(inputStream);
		bufferedReader = new BufferedReader(inputStreamReader);
		reader = new JsonReader(inputStreamReader);
		Latitude= new ArrayList<Double>();
		Longitude= new ArrayList<Double>();
		localname=new ArrayList<String>();
		priority=new ArrayList<Integer>();
		clicks=new ArrayList<Integer>();
		name=new ArrayList<String>();
		color=new ArrayList<Integer>();
		Short=new ArrayList<String>();
		start=new ArrayList<String>();
		stop=new ArrayList<String>();
		first_name=new ArrayList<String>();
		profile=new ArrayList<String>();
	}
	void parse() throws IOException
	{
		try
		{
			reader.beginArray();
			while (reader.hasNext())
			{
				reader.beginObject();
		        while (reader.hasNext()) {
		        	 String key = reader.nextName();
		        	 if(key.equals("location"))
		        		 parseLocation(reader.nextString());
		        	 else if(key.equals("localname")){
		        		 localname.add(reader.nextString());
		        		 Log.v("localname", localname.toString());
		        	 }
		        	 else if(key.equals("priority")) {
		        		 priority.add(reader.nextInt());
		        		 Log.v("priority", priority.toString());
		        	 }
		        	 else if(key.equals("clicks")){
		        		 clicks.add(reader.nextInt());
		        		 Log.v("clicks", clicks.toString());
		        	 }	
		        	 else if(key.equals("color")){
		        		 //color.add(reader.nextString());
		        		 mapImageWithColor(reader.nextString());
		        		 Log.v("color",color.toString());
		        	 }
		        	 else if(key.equals("name")) {
		        		 name.add(reader.nextString());
		        		 Log.v("name", name.toString());
		        	 }
		        	 else if(key.equals("short")) {
		        		 Short.add(reader.nextString());
		        		 Log.v("Short", Short.toString()); 
		        	 }	 
		        	 else if(key.equals("start")){
		        		 start.add(reader.nextString());
		        		 Log.v("start", start.toString()); 
		        	 }
		        	 else if(key.equals("stop")){
		        		 stop.add(reader.nextString());
		        		 Log.v("start", stop.toString()); 
		        	 }
		        	 else if(key.equals("source")) {
		        		 reader.beginObject();
		        		 while (reader.hasNext()) {
		        			 String key1 = reader.nextName();
		        			 if(key1.equals("first_name")&&reader.peek()!=JsonToken.NULL){
		        				 first_name.add(reader.nextString());
		        				 Log.v("first_name", first_name.toString());
		        			 }
		        			 else if(key1.equals("profile")&&reader.peek()!=JsonToken.NULL) {
		        				 profile.add(reader.nextString());
		        				 Log.v("profile", profile.toString());
		        			 }
		        			 else 
		        			  reader.skipValue();
		        		 }
		        		 reader.endObject();
		        	 }
		        	 else 
		                reader.skipValue();
		        }
		        reader.endObject();   
	    	}
			reader.endArray();
			
		}
		finally
		{
		    reader.close();
		}
	}
	
	void parseLocation(String location)
	{
		Log.v("Location",location);
		 Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(location);
	     while(m.find()) {
	      // System.out.println(m.group(1));  
	    	 String[] splited=m.group(1).split("\\s+");
	    	 Log.v("Splied strings", splited[0]+","+splited[1]);
	    	 double lat= Double.valueOf(splited[0]);
	    	 Log.v("Splied lat", String.valueOf(lat));
	    	 Scanner sc = new Scanner(m.group(1));
	    	 
	    	 if(sc.hasNextDouble())
	    	 {
	    	     Longitude.add(sc.nextDouble());
	    		 Latitude.add(sc.nextDouble());
	    	 }
	    	 Log.v("Latitude",Latitude.toString());
	    	 Log.v("Longitude",Longitude.toString());
	    	 
       		
	     }
	}
	
	public void mapImageWithColor(String _color)
	{
	 switch(_color)
	 {
	 case "blue":
		  color.add(R.drawable.marker);
		   break;
	 case "orange":
		  color.add(R.drawable.orange);
		   break;
	 case "indigo":
		  color.add(R.drawable.indigo);
		   break;
	 case "brown":
		  color.add(R.drawable.brown);
		   break;
	 case "purple":
		  color.add(R.drawable.purple);
		   break;
	 case "green":
		  color.add(R.drawable.green);
		   break;
	 case "pink":
		  color.add(R.drawable.pink);
		   break;
	 default:
	       throw new IllegalArgumentException();
   
		   
	 }
	}
   ArrayList getLatitude()
	{
		if(!Latitude.isEmpty())
			return Latitude;
		return null;
	}
   
   ArrayList getLongitude()
	{
		if(!Longitude.isEmpty())
			return Longitude;
		return null;
	}
   
   ArrayList getLocalname()
  	{
  		if(!localname.isEmpty())
  			return localname;
  		return null;
  	}
   
   ArrayList getPriority()
 	{
 		if(!priority.isEmpty())
 			return priority;
 		return null;
 	}
   
   ArrayList getClicks()
   {
	   if(!clicks.isEmpty())
			return clicks;
		return null;
   }
   
   ArrayList getNames()
   {
	   if(!name.isEmpty())
			return name;
		return null;
   }
   
   ArrayList getColors()
   {
	   if(!color.isEmpty())
			return color;
		return null;
   }
   
   ArrayList getShorts()
   {
	   if(!Short.isEmpty())
			return Short;
		return null;
   }
   
   ArrayList getStarts()
   {
	   if(!start.isEmpty())
			return start;
		return null;
   }
   
   ArrayList getStops()
   {
	   if(!stop.isEmpty())
			return stop;
		return null;
   }
   
   ArrayList getFirstNames()
   {
	   if(!first_name.isEmpty())
			return first_name;
		return null;
   }
   
   ArrayList getProfiles()
   {
	   if(!profile.isEmpty())
			return profile;
		return null;
   }
   
   
}
