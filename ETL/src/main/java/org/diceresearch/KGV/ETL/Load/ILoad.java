package org.diceresearch.KGV.ETL.Load;

import java.io.IOException;

public interface ILoad <U,D>{
    void Save(U forSave,D destination) throws IOException, ClassNotFoundException;
}