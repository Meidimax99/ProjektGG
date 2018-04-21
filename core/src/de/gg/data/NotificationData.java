package de.gg.data;

public class NotificationData {

	private String title, text;
	private NotificationIcon icon;

	public NotificationData(String title, String text, NotificationIcon icon) {
		super();
		this.title = title;
		this.text = text;
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public NotificationIcon getIcon() {
		return icon;
	}

	public enum NotificationIcon {

	}

}
