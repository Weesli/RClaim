package net.weesli.rclaim.database.interfaces;

import net.weesli.rclaim.api.enums.StorageType;

public interface IDatabase{

    StorageType getStorageType();
    String tableSQL();
}
