package eu.project.rapid.handtrackerapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import eu.project.rapid.handtracker.HandTracker;

class HandTrackerAppSerialization
{
	static HandTracker switchHT(HandTracker ht) throws IOException, ClassNotFoundException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bos);
		os.writeObject(ht);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream is = new ObjectInputStream(bis);
		return (HandTracker)is.readObject();
	}
	
	public static void main(String [] args) throws Exception
	{
		HandTracker tr = new HandTracker(null, true);

		tr.step1_grabRAPID();
		tr = switchHT(tr);

		while (true)
		{
			tr.step8_visualizeRAPID(tr.getDefaultInitPosRAPID());
		}
	}
}