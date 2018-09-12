package io.sitoolkit.wt.domain.pageload;

public class ElementId {

    private int nth;

    private String id;

    public ElementId(int nth, String id) {
        super();
        this.nth = nth;
        this.id = id;
    }

    public int getNth() {
        return nth;
    }

    public void setNth(int nth) {
        this.nth = nth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + nth;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ElementId other = (ElementId) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (nth != other.nth)
            return false;
        return true;
    }
}
