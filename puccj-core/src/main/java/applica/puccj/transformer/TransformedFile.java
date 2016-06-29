package applica.puccj.transformer;

/**
 * Created by bimbobruno on 13/10/15.
 */
public class TransformedFile {

    private String classInternalName;
    private byte[] bytes;

    public TransformedFile(String classInternalName, byte[] bytes) {
        this.classInternalName = classInternalName;
        this.bytes = bytes;
    }

    public String getClassInternalName() {
        return classInternalName;
    }

    public void setClassInternalName(String classInternalName) {
        this.classInternalName = classInternalName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
