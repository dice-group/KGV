package Transform;

public interface ITransform<T,U> {
    public T Transform (U input, String splitter) throws Exception;
}
