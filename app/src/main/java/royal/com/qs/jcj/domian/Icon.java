package royal.com.qs.jcj.domian;

/**
 * Created by jcj on 15/12/18.
 */
public class Icon {
    private String name;
    private int resourceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String toString() {
        return "Icon{" +
                "name='" + name + '\'' +
                ", resourceId=" + resourceId +
                '}';
    }

    public Icon(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }
}
