package org.diceresearch.KGV.ETL.Transform;

public interface ITransform<T,U> {
    public T transform(U input, String splitter) throws Exception;
}
