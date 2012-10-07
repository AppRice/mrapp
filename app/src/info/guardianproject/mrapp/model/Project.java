package info.guardianproject.mrapp.model;

import java.util.ArrayList;

import info.guardianproject.mrapp.db.ProjectsProvider;
import info.guardianproject.mrapp.db.StoryMakerDB;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Project {
	protected Context context;
	protected int id;
	protected String title;
	protected String thumbnailPath;
	
	public Project(Context context) {
		this.context = context; 
	}
	
	public Project(Context context, Cursor cursor) {
		this.context = context;
		id = cursor.getInt(cursor.getColumnIndex(StoryMakerDB.Schema.Projects.ID)); // FIXME use indexes directly to optimize this later
		thumbnailPath = cursor.getString(cursor.getColumnIndex(StoryMakerDB.Schema.Projects.COL_THUMBNAIL_PATH)); // FIXME use indexes directly to optimize this later
		title = cursor.getString(cursor.getColumnIndex(StoryMakerDB.Schema.Projects.COL_TITLE)); // FIXME use indexes directly to optimize this later
	}
	
	public void save() {
		ContentValues values = new ContentValues();
		values.put(StoryMakerDB.Schema.Projects.COL_TITLE, title);
		values.put(StoryMakerDB.Schema.Projects.COL_THUMBNAIL_PATH, thumbnailPath);
		Uri uri = context.getContentResolver().insert(ProjectsProvider.PROJECTS_CONTENT_URI, values);
		// FIXME grab out the id and set it on ourself
	}
    
    public static Cursor getAsCursor(Context context, int id) {
        String selection = StoryMakerDB.Schema.Projects.ID + "=?";
        String[] selectionArgs = new String[] { "" + id};
        return context.getContentResolver().query(ProjectsProvider.PROJECTS_CONTENT_URI, null, selection, selectionArgs, null);
    }
	
	public static Project get(Context context, int id) {
	    return new Project(context, Project.getAsCursor(context, id));
	}
	
	public static Cursor getAllAsCursor(Context context) {
		return context.getContentResolver().query(ProjectsProvider.PROJECTS_CONTENT_URI, null, null, null, null);
	}
	
	public static ArrayList<Project> getAllAsList(Context context) {
		ArrayList<Project> projects = new ArrayList<Project>();
		Cursor cursor = getAllAsCursor(context);
		if (cursor.moveToFirst()) {
			do {
				projects.add(new Project(context, cursor));
			} while(cursor.moveToNext());
		}
		return projects;
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the thumbnailPath
	 */
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	/**
	 * @param thumbnailPath the thumbnailPath to set
	 */
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	
}
