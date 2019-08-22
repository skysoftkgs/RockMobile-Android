package org.church.rockmobile.common;

import java.util.HashMap;

import java.util.Observer;
import java.util.Observable;

public class NotificationService {

	private class NotificationObservable extends Observable
	{
		public void notifyObservers()
		{
			this.setChanged();
			
			super.notifyObservers();
		}
		
		public void notifyObservers(Object data)
		{
			this.setChanged();
			
			super.notifyObservers(data);
		}
	}
	
    private HashMap<String, NotificationObservable> observables = new HashMap<String, NotificationObservable>();
    

	/**
	 * singleton class instance.
	 */
	private static volatile NotificationService sInstance = null;

    private NotificationService()
    {
	}

    public static NotificationService getInstance()
	{
		if (sInstance == null)
		{
			sInstance = new NotificationService();
		}

		return sInstance;
	}

    public void addObserver(String notification, Observer observer)
    {
    	NotificationObservable observable = observables.get(notification);
    	if (observable == null)
    	{
    		observable = new NotificationObservable();
    		observables.put(notification, observable);
    	}
    	observable.addObserver(observer);
    }

    public void removeObserver(String notification, Observer observer)
    {
    	Observable observable = observables.get(notification);
    	if (observable != null)
    	{         
    		observable.deleteObserver(observer);
    	}
    }       

    public void postNotification(String notification, Object object)
    {
    	NotificationObservable observable = observables.get(notification);
    	if (observable != null)
    	{
    		if (object == null)
    		{
    			observable.notifyObservers();
    		}
    		else
    		{
    			observable.notifyObservers(object);
    		}
    	}
    }
}
