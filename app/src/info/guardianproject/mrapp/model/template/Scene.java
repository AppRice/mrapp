package info.guardianproject.mrapp.model.template;

import java.util.ArrayList;

public class Scene {

    public String mTitle;
    ArrayList<Clip> mArrayClips;
    
    public Scene()
    {
        
    }
    
    public void setDefaults()
    {
        mTitle="Your scene";
    }
    
    public ArrayList<Clip> getClips()
    {
        return mArrayClips;
    }
    
    public Clip getClip(int idx) {
        return mArrayClips.get(idx);
    }

    public void addClip(Clip clip)
    {
        if (mArrayClips == null) {
            mArrayClips = new ArrayList<Clip>();
        }
        
        mArrayClips.add(clip);
    }
}