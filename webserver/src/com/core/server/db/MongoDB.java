package com.core.server.db;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Created by chen_lin on 2017/11/24.
 */
public class MongoDB {
    private MongoClient mgo = null;

    public MongoDB() {
    }

    public MongoDatabase getDB() throws Exception {
        if(DBUtils.mgo_dbname == null || DBUtils.mgo_dbname.length() <= 0) {
            DBUtils.initDB();
        }

        this.mgo = new MongoClient(DBUtils.mgo_host, DBUtils.mgo_port);
        return this.mgo.getDatabase(DBUtils.mgo_dbname);
    }

    public void closeMongoDB() {
        if(this.mgo != null) {
            this.mgo.close();
        }

        this.mgo = null;
    }
}
