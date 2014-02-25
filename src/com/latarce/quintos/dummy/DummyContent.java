package com.latarce.quintos.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.latarce.kintos14.R;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();
	
	public static List<HashMap<String, String>> ITEMS_ARRAYLIST_MAPS = new ArrayList<HashMap<String, String>>();
	
	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "Programa", Integer.toString(R.drawable.programa)));
		addItem(new DummyItem("2", "Cómo llegar", Integer.toString(R.drawable.gps)));
		addItem(new DummyItem("3", "Calendario", Integer.toString(R.drawable.calendar)));
		addItem(new DummyItem("4", "Chat", Integer.toString(R.drawable.chat)));
		addItem(new DummyItem("5", "Compartir App", Integer.toString(android.R.drawable.ic_menu_share)));
		addItem(new DummyItem("6", "Acerca de...", Integer.toString(R.drawable.news)));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("texto", item.content);
		map.put("imagen", item.image);
		ITEMS_ARRAYLIST_MAPS.add(map);
		
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String content;
		public String image;

		public DummyItem(String id, String content, String image) {
			this.id = id;
			this.content = content;
			this.image = image;
		}

		@Override
		public String toString() {
			return content;
		}
	}
}
