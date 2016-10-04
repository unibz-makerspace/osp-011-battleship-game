package it.unibz.inf.makerspace.battleship.game;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.SwingWorker;

public abstract class GameStats {
	
	public static class Info implements Serializable, Comparable<Info> {
		
		private static final long serialVersionUID = -8020646543175190197L;
		
		public final PlayerInfo playerInfo;
		// TODO add gameinfo
		//public final GameInfo gameInfo;
		
		public Info(PlayerInfo playerInfo) {
			this.playerInfo = playerInfo;
		}

		@Override
		public int compareTo(Info o) {
			// TODO change comparison
			return Long.compare(
					Long.parseLong(playerInfo.entryDate),
					Long.parseLong(o.playerInfo.entryDate));
		}
	}
	
	private static final File gameFolder = new File(
			System.getProperty("user.home") +
			File.separator + "Battleship");
	
	private static final String fileExtension = ".txt";
	
	public static void saveGameStatsInfo(Info info) {
		if (gameFolder.exists() || gameFolder.mkdirs()) {
			try {
				FileOutputStream file = new FileOutputStream(
						gameFolder.getAbsolutePath() + File.separator +
						info.playerInfo.entryDate + fileExtension);
				ObjectOutputStream out = new ObjectOutputStream(file);
		         out.writeObject(info);
		         out.close();
		         file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static HashMap<String, Info> getGameStatsInfos() {
		HashMap<String, Info> hmap = new HashMap<String, Info>();
		for (File file : gameFolder.listFiles()) {
			Info info = getGameStatsInfo(file);
			if (info != null) {
				hmap.put(info.playerInfo.entryDate, info);
			}
		}
		return hmap;
	}
	
	public static Info getGameStatsInfo(File gameStatsFile) {
        try {
        	FileInputStream file = new FileInputStream(gameStatsFile);
            ObjectInputStream in = new ObjectInputStream(file);
            Info info = (Info) in.readObject();
            in.close();
			file.close();
			return info;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public static HashMap<String, Info> sortByGameStats(
			HashMap<String, Info> map) {
		List<Entry<String, Info>> list = new LinkedList<Entry<String, Info>>(
				map.entrySet());
		// Custom Comparator.
		Collections.sort(list, new Comparator<Entry<String, Info>>() {
			@Override
			public int compare(Entry<String, Info> o1, Entry<String, Info> o2) {
				final Info i1 = o1.getValue();
				final Info i2 = o2.getValue();
				return i1.compareTo(i2);
			}
		});
		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap<String, Info> sortedHashMap = new LinkedHashMap<String, Info>();
		for (Iterator<Entry<String, Info>> it = list.iterator(); it.hasNext();) {
			Entry<String, Info> entry = it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
	
	public interface FileListener {
		void onFileCreated(String fileName);
		void onFileModified(String fileName);
		void onFileDeleted(String fileName);
	}
	
	public static class FileWorker extends SwingWorker<Void, PropertyChangeEvent> {
		
	    public static final String FILE_CREATED =
	    		StandardWatchEventKinds.ENTRY_CREATE.name();
	    public static final String FILE_MODIFIED =
	    		StandardWatchEventKinds.ENTRY_MODIFY.name();
	    public static final String FILE_DELETED =
				StandardWatchEventKinds.ENTRY_DELETE.name();
 
	    private Path directory;
	    private WatchService watcher;
	    private FileListener fileListener;

	    public FileWorker() throws IOException {
	        directory = gameFolder.toPath();
	        watcher = FileSystems.getDefault().newWatchService();
	        directory.register(
	        		watcher,
	        		StandardWatchEventKinds.ENTRY_CREATE,
	        		StandardWatchEventKinds.ENTRY_DELETE,
	        		StandardWatchEventKinds.ENTRY_MODIFY);
	        fileListener = null;
	    }
	    
	    public void setGameStatsFileListener(FileListener fileListener) {
	    	this.fileListener = fileListener;
	    }

	    @SuppressWarnings("unchecked")
		@Override
	    protected Void doInBackground() throws Exception {
	        for (;;) {
	            // wait for key to be signaled
	            WatchKey key;
	            try {
	                key = watcher.take();
	            } catch (InterruptedException x) {
	                return null;
	            }

	            for (WatchEvent<?> event : key.pollEvents()) {
	                WatchEvent.Kind<?> kind = event.kind();
	                // TBD - provide example of how OVERFLOW event is handled
	                if (kind == StandardWatchEventKinds.OVERFLOW) {
	                    continue;
	                }
	                publish(createChangeEvent((WatchEvent<Path>) event, key));
	            }

	            // reset key return if directory no longer accessible
	            boolean valid = key.reset();
	            if (!valid) {
	                break;
	            }
	        }
	        return null;
	    }

	    /**
	     * Creates and returns the change notification. This method is called from the 
	     * worker thread while looping through the events as received from the Watchkey.
	     * 
	     * @param event
	     * @param key
	     */
	    protected PropertyChangeEvent createChangeEvent(WatchEvent<Path> event, WatchKey key) {
	        Path name = event.context();
	        // real world will lookup the directory from the key/directory map
	        Path child = directory.resolve(name);
	        PropertyChangeEvent e = new PropertyChangeEvent(this, event.kind().name(), null, child.toFile());
	        return e;
	    }

	    @Override
	    protected void process(List<PropertyChangeEvent> chunks) {
	        super.process(chunks);
	        for (PropertyChangeEvent event : chunks) {
	            getPropertyChangeSupport().firePropertyChange(event);
	            if (fileListener != null) {
	            	final String propertyName = event.getPropertyName();
	            	if (FILE_CREATED.equals(propertyName)) {
	            		fileListener.onFileCreated(
	            				event.getNewValue().toString());
	            	} else if(FILE_MODIFIED.equals(propertyName)) {
	            		fileListener.onFileModified(
	            				event.getNewValue().toString());
	            	} else if(FILE_DELETED.equals(propertyName)) {
	            		fileListener.onFileDeleted(
	            				event.getNewValue().toString());
	            	}
	            }
	        }
	    }
	}
}
