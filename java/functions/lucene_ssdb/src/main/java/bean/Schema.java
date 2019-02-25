package bean;

import java.util.List;

public class Schema {

    private String index;
    private String analyser;
    private List<Field> fields;
    private Ssdb ssdb;

    public Schema() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getAnalyser() {
        return analyser;
    }

    public void setAnalyser(String analyser) {
        this.analyser = analyser;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Ssdb getSsdb() {
        return ssdb;
    }

    public void setSsdb(Ssdb ssdb) {
        this.ssdb = ssdb;
    }
}

