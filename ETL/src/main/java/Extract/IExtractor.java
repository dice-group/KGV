package Extract;

import java.io.IOException;

public interface IExtractor <T,U>{
    public T Extract(U from) throws IOException, ClassNotFoundException;
}
