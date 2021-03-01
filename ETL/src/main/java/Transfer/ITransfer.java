package Transfer;

import Model.Correctness;

import java.io.File;
import java.io.FileNotFoundException;

public interface ITransfer<T,U> {
    public T Transfer (U input, String splitter) throws Exception;
}
